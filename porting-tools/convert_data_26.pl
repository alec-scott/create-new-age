#!/usr/bin/env perl
# Converts Create: New Age's 1.21.1 NeoForge data (src/generated/resources/data and the monkey-edition builtin pack under
# src/main/resources/resourcepacks) to 26.1 / Create Fly formats. Datagen is stubbed, so this stands in for the recipe
# provider. Adapted from the Crafts & Additions and Steam n Rails converters.
#
# Run once from the repo root, on the unconverted data: perl porting-tools/convert_data_26.pl (not safe to rerun)
#
# - recipes: ingredient objects ({"item"}/{"tag"}) -> "id" / "#tag"; Create processing recipes to Create Fly's codecs:
#   "ingredients": [x] -> "ingredient": x (crushing, cutting, pressing, New Age energising); deploying and
#   item_application -> target + ingredient; mixing/compacting fluid ingredients split into fluid_ingredients
#   (mB -> droplets, x81); sequenced_assembly -> result/junks and "$ingredient"/"$result" step placeholders.
# - neoforge:conditions -> fabric:load_conditions (neoforge:never -> fabric:not(fabric:true)).
use strict;
use warnings;
use File::Find;
use JSON::PP;

my $json = JSON::PP->new->utf8->canonical->pretty->indent_length(2)->space_before(0);
my @recipe_dirs = ('src/generated/resources/data/create_new_age/recipe',
    glob('src/main/resources/resourcepacks/*/data/create_new_age/recipe'));
my %warned;
my %count;

sub warn_once { my $m = shift; print STDERR "warning: $m\n" unless $warned{$m}++; }
sub read_json { my $f = shift; open my $in, '<:raw', $f or die "$f: $!"; local $/; my $d = $json->decode(<$in>); close $in; return $d; }
sub write_json { my ($f, $d) = @_; open my $out, '>:raw', $f or die "$f: $!"; print $out $json->encode($d); close $out; }
sub files_under { my $dir = shift; my @f; find(sub { push @f, $File::Find::name if /\.json$/ }, $dir) if -d $dir; return sort @f; }

sub ingredient {
    my $in = shift;
    return $in unless ref $in;
    if (ref $in eq 'ARRAY') {
        my @alts = map { ingredient($_) } @$in;
        return $alts[0] if @alts == 1;
        return \@alts unless grep { /^#/ } @alts;
        warn_once("alternative tags reduced to the first: @alts");
        return $alts[0];
    }
    return $in->{item} if exists $in->{item};
    return "#$in->{tag}" if exists $in->{tag};
    die 'unknown ingredient ' . $json->encode($in);
}

sub is_fluid_ing { my $x = shift; return ref $x eq 'HASH' && ($x->{type} // '') =~ /^(neoforge:(tag|single)|fluid_stack|fluid)$/; }

sub fluid_ingredient {
    my $f = shift;
    my $amount = $f->{amount} * 81;
    return { type => 'fluid_tag', amount => $amount, fluid_tag => "#" . ($f->{tag} // $f->{fluid_tag}) } if exists $f->{tag} || exists $f->{fluid_tag};
    return { type => 'fluid_stack', amount => $amount, fluid => ($f->{fluid} // $f->{id}) };
}

sub output {
    my $r = shift;
    return { id => $r } unless ref $r;
    my %out = (id => $r->{item} // $r->{id});
    $out{count} = $r->{count} if ($r->{count} // 1) != 1;
    $out{chance} = $r->{chance} if exists $r->{chance} && $r->{chance} != 1;
    $out{components} = $r->{components} if exists $r->{components};
    return \%out;
}

sub convert_condition {
    my $c = shift;
    my $t = $c->{type};
    return { condition => 'fabric:all_mods_loaded', values => [ $c->{modid} ] } if $t eq 'neoforge:mod_loaded';
    return { condition => 'fabric:not', value => { condition => 'fabric:true' } } if $t eq 'neoforge:never';
    die "unhandled condition $t";
}

sub load_conditions {
    my @c = map { convert_condition($_) } @{ shift() };
    return $c[0] if @c == 1;
    return { condition => 'fabric:and', values => \@c };
}

sub sequence_step {
    my $s = shift;
    my $type = $s->{type};
    my @ing = @{ $s->{ingredients} // [] };
    if ($type eq 'create:deploying') {
        my %out = (type => $type, target => '$ingredient', ingredient => ingredient($ing[1]), results => ['$result']);
        $out{keep_held_item} = JSON::PP::true if $s->{keep_held_item};
        return \%out;
    }
    return { type => $type, ingredient => '$ingredient', results => ['$result'] } if $type eq 'create:pressing';
    return { type => $type, ingredient => '$ingredient', results => ['$result'], processing_time => $s->{processing_time} // 50 }
        if $type eq 'create:cutting';
    return { type => $type, ingredient => '$ingredient', results => ['$result'], energy_needed => $s->{energy_needed} }
        if $type eq 'create_new_age:energising';
    die "unsupported sequence step $type";
}

sub convert_recipe {
    my $r = shift;
    my %out;
    $out{'fabric:load_conditions'} = load_conditions($r->{'neoforge:conditions'}) if $r->{'neoforge:conditions'};
    return \%out unless $r->{type}; # condition-only override (neoforge:never)
    my $type = $r->{type};
    $out{type} = $type;
    for my $k (qw(category group show_notification)) { $out{$k} = $r->{$k} if exists $r->{$k}; }
    my @ing = ref $r->{ingredients} eq 'ARRAY' ? @{ $r->{ingredients} } : ();

    if ($type eq 'minecraft:crafting_shaped' || $type eq 'create:mechanical_crafting') {
        $out{pattern} = $r->{pattern};
        $out{key} = { map { $_ => ingredient($r->{key}{$_}) } keys %{ $r->{key} } };
        $out{accept_mirrored} = $r->{accept_mirrored} if exists $r->{accept_mirrored};
        $out{result} = output($r->{result});
    } elsif ($type eq 'minecraft:crafting_shapeless') {
        $out{ingredients} = [ map { ingredient($_) } @ing ];
        $out{result} = output($r->{result});
    } elsif ($type eq 'create:mixing' || $type eq 'create:compacting') {
        my @items = grep { !is_fluid_ing($_) } @ing;
        my @fluids = grep { is_fluid_ing($_) } @ing;
        $out{ingredients} = [ map { ingredient($_) } @items ] if @items;
        $out{fluid_ingredients} = [ map { fluid_ingredient($_) } @fluids ] if @fluids;
        $out{results} = [ map { output($_) } @{ $r->{results} } ];
        $out{heat_requirement} = $r->{heat_requirement} if exists $r->{heat_requirement};
        $out{processing_time} = $r->{processing_time} if exists $r->{processing_time};
    } elsif ($type eq 'create:crushing') {
        $out{ingredient} = ingredient($ing[0]);
        $out{processing_time} = $r->{processing_time} // 100;
        $out{results} = [ map { output($_) } @{ $r->{results} } ];
    } elsif ($type eq 'create:cutting') {
        $out{ingredient} = ingredient($ing[0]);
        $out{processing_time} = $r->{processing_time} // 50;
        $out{results} = [ map { output($_) } @{ $r->{results} } ];
    } elsif ($type eq 'create:pressing') {
        $out{ingredient} = ingredient($ing[0]);
        $out{results} = [ map { output($_) } @{ $r->{results} } ];
    } elsif ($type eq 'create:deploying' || $type eq 'create:item_application') {
        $out{target} = ingredient($ing[0]);
        $out{ingredient} = ingredient($ing[1]);
        $out{results} = [ map { output($_) } @{ $r->{results} } ];
        $out{keep_held_item} = JSON::PP::true if $r->{keep_held_item};
    } elsif ($type eq 'create_new_age:energising') {
        $out{ingredient} = ingredient($ing[0]);
        $out{results} = [ map { output($_) } @{ $r->{results} } ];
        $out{energy_needed} = $r->{energy_needed};
    } elsif ($type eq 'create:sequenced_assembly') {
        my @results = map { output($_) } @{ $r->{results} };
        $out{ingredient} = ingredient($r->{ingredient});
        $out{transitional_item} = { id => $r->{transitional_item}{id} // $r->{transitional_item}{item} };
        $out{result} = shift @results;
        $out{junks} = \@results if @results;
        $out{loops} = $r->{loops} // 1;
        $out{sequence} = [ map { sequence_step($_) } @{ $r->{sequence} } ];
        die 'sequence must have at least two steps' if @{ $out{sequence} } * $out{loops} < 2;
    } else {
        die "unsupported recipe type $type";
    }
    return \%out;
}

for my $dir (@recipe_dirs) {
    for my $f (files_under($dir)) {
        write_json($f, convert_recipe(read_json($f)));
        $count{'recipes converted'}++;
    }
}
print join(', ', map { "$count{$_} $_" } sort keys %count) . "\n";
