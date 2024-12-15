#!/bin/sh

DEPLOY_VERSION="2.0.9-SNAPSHOT"

CURRENT_VERSION=$(sed -n 's/.*<free-module.version>\([^\<]*\)<\/free-module.version>.*/\1/p' "free-module-bom/pom.xml")

# 检查是否成功提取版本号
if [[ -z "$CURRENT_VERSION" ]]; then
  echo "未找到 free-module.version 版本号"
  exit 1
fi

echo "当前版本号: $CURRENT_VERSION"

sed -i '' "s|<free-module.version>$CURRENT_VERSION</free-module.version>|<free-module.version>$DEPLOY_VERSION</free-module.version>|" free-module-bom/pom.xml
sed -i '' "s|<version>$CURRENT_VERSION</version>|<version>$DEPLOY_VERSION</version>|" free-module-bom/pom.xml
sed -i '' "s|<version>$CURRENT_VERSION</version>|<version>$DEPLOY_VERSION</version>|" free-module-core/pom.xml
sed -i '' "s|<version>$CURRENT_VERSION</version>|<version>$DEPLOY_VERSION</version>|" free-module-starter/pom.xml
sed -i '' "s|<version>$CURRENT_VERSION</version>|<version>$DEPLOY_VERSION</version>|g" pom.xml

echo "更新后版本号: $DEPLOY_VERSION"