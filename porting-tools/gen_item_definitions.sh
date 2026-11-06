#!/usr/bin/env bash
# Writes a 26.1 item model definition (assets/create_new_age/items/<id>.json) for every committed top-level
# models/item/<id>.json. Datagen is stubbed, so this stands in for RegistrateItemModelProvider.run.
# Run from the repo root.
set -euo pipefail

out=src/generated/resources/assets/create_new_age/items
mkdir -p "$out"

for dir in src/generated/resources/assets/create_new_age/models/item src/main/resources/assets/create_new_age/models/item; do
    [ -d "$dir" ] || continue
    for f in "$dir"/*.json; do
        id=$(basename "$f" .json)
        printf '{\n  "model": {\n    "type": "minecraft:model",\n    "model": "create_new_age:item/%s"\n  }\n}\n' "$id" > "$out/$id.json"
    done
done

echo "$(ls "$out" | wc -l) item definitions in $out"
