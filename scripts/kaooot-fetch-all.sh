cd ../src/main/resources/gamedata/kaooot/

BASE_URL="https://github.com/Kaooot/bedrock-network-data/raw/refs/heads/master/preview/1.21.110.26/"

FILES=(
  "biome_definitions.nbt"
  "biomes.json"
  "block_tags.json"
  "creative_items.json"
  "entity_identifiers.nbt"
  "item_components.nbt"
  "item_tags.json"
  "recipes.json"
  "trim_data.json"
)

for FILE in "${FILES[@]}"; do
  echo "Fetching $FILE..."
  wget -O $FILE "$BASE_URL$FILE" > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Failed to fetch $FILE"
  else
    echo "Successfully fetched $FILE"
  fi
done