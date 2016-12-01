#!/bin/bash
# ======================================================================
# Env setting script
# ======================================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd -P)"

JAVA_DIR=${BASE_DIR}/java

JDK_32_HOME=${JAVA_DIR}/linux32
JDK_64_HOME=${JAVA_DIR}/linux64

unamestr=`uname`
if [[ "$unamestr" == 'Linux' ]]; then
  if [ -d "$JDK_32_HOME" ]; then
        echo "${JDK_32_HOME}"
        export JAVA_HOME=${JDK_32_HOME}
  fi

  if [ -d "$JDK_64_HOME" ]; then
        echo "${JDK_64_HOME}"
        export JAVA_HOME=${JDK_64_HOME}
  fi
elif [[ "$unamestr" == 'Darwin' ]]; then
  echo "你正在使用MAC OS, 请自行准备版本8以上的JDK, 如果已经安装请忽略本信息。"
fi
