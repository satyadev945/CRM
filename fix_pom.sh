#!/bin/bash
count=0
while IFS= read -r line; do
  count=$((count + 1))
  if [ $count -eq 11 ]; then
    echo "    <name>crm</name>"
  else
    echo "$line"
  fi
done < /modernize-data/studio-data/TNT1001/APP1083/transformed-code/3/studio-workspace/Java-CRM/pom.xml > /modernize-data/studio-data/TNT1001/APP1083/transformed-code/3/studio-workspace/Java-CRM/pom.xml.fixed2
