#!/bin/bash
# ======================================================================
# NAMI Tools 服务器管理工具
# ======================================================================

BACKGROUD="\033[37m"                            # echo 时的 背景颜色
FONTCOLOR="\033[33m"                            # echo 时的 字体颜色
END="\033[0m"                                   # echo 时的 结束标志
HOST="127.0.0.1"                                # 本机地址用于测试本机Tomcat是否已经启动完成
STATUS="200 301 302 404"                        # 定义正常访问返回的状态码
SCRIPT_NAME=$0                                  # 脚本绝对路径
START_STOP=$1                                   # 命令行第一个参数

ROOT_DIR="$(cd "$(dirname "$0")" && pwd -P)"
echo ${ROOT_DIR}

INSTALLATION_DIR=${ROOT_DIR}
TOMCAT_HOME=${INSTALLATION_DIR}/appserver/tomcat
NAMI_CATALINA_BASE=${INSTALLATION_DIR}/appserver/CATALINA_BASE

. ${INSTALLATION_DIR}/java/set_env.sh

export CATALINA_HOME=${TOMCAT_HOME}
export CATALINA_BASE=${NAMI_CATALINA_BASE}

echo "JAVA_HOME:		${JAVA_HOME}"
echo "CATALINA_BASE: ${CATALINA_BASE}"
echo "CATALINA_HOME: ${CATALINA_HOME}"

usage(){
    echo
    echo "Usage: ${SCRIPT_NAME} start|stop|restart|status"
    echo
    exit 1
}
# 判断命令行参数是否为空,如果为空，调用帮助提示函数
if [[ ${START_STOP} == "" ]]; then
    usage
fi

# 获取PID函数
getPID(){
	PID=$(ps -ef | grep -v 'grep' | grep "${NAMI_CATALINA_BASE}/conf/logging.properties" | awk '{print $2}')
}

# 得到端口
getPort(){
    PORT=`cat ${NAMI_CATALINA_BASE}/conf/server.xml | awk '/HTTP\/1.1/ {print $2}' | cut -d'"' -f2`
}

# 输出颜色
printColor(){
    echo -ne 		     "*********************************************************\n"
	echo -ne "${FONTCOLOR}****************** NAMI Server Management  **************${END}\n"
	echo -ne "${FONTCOLOR}****************** Born for Wechat Applet  **************${END}\n"
	echo -ne 			 "*********************************************************\n"
	echo
}
# 输出正在启动Tomcat
printStart(){
    printColor
    echo -n "NAMI Tomcat is starting please wait ..."
	echo
}
# 输出正在停止Tomcat
printStop(){
    printColor
    echo "NAMI Tomcat is stopping,please wait ..."
	echo
}
# 输出Tomcat没有运行
printNotRun(){
    printColor
    echo "NAMI Tomcat is not running..."
	echo
}
# 输出Tomcat正在运行
printRunning(){
    printColor
    echo "NAMI Tomcat is running... PID: ${PID}"
	echo
}

# 输出Tomcat没有，试图启动Tomcat
printNotRunTryStart(){
    printColor
    echo "NAMI Tomcat is not running, trying start NAMI Tomcat."
	echo
}

# 输出端口被占用
printPortUsed(){
    printColor
    echo "Port: ${PORT} is used by other application, please check."
	echo
}

# sleep 函数
sleepFun(){
    sleep 0.5
}

# 测试端口是否可以正常访问
testPortIsOk(){
    PORT_OK=1
    status=`/usr/bin/curl -I $1 2>/dev/null | head -1 | cut -d" " -f2`
    for i in $STATUS; do
        if [[ ${i} == ${status} ]]; then
            PORT_OK=0
        fi
    done
    return ${PORT_OK}
}

#检查端口是否被占用，如果占用输出1，如果没有被占用输入0
checkPortUsed(){
	pIDa=`netstat -an|grep ${PORT}|grep -i LISTEN | awk '{print $2}'`
	
	if [[ "${pIDa}X" != "X" ]]; then
		printPortUsed
		PORT_USED="0"
	fi
}

# 检查每个Tomcat是否已经启动好
checkTomcat(){
	echo "Checking http://${HOST}:${PORT}".
    status_ok=1
    while [[ ${status_ok} == 1 ]]; do
        testPortIsOk http://${HOST}:${PORT}
        if [[ $? == 0 ]]; then
            echo " start OK!"
            status_ok=0
        else
            echo -n "."
        fi
    done
}

# 单个Tomcat启动函数
startSingleTomcat(){
	checkPortUsed
	
	if [[ "${PORT_USED}X" != "X" ]]; then
		printPortUsed
	else 
		. ${CATALINA_HOME}/bin/catalina.sh start
		printStart
		checkTomcat
	fi
}

# Tomcat启动函数
start(){
	getPID
	getPort
	
	if [[ "${PID}X" != "X" ]]; then
		printRunning
	else
		startSingleTomcat
	fi
}
# Tomcat停止函数
stop(){
	getPID
	if [[ "${PID}X" == "X" ]]; then
		printNotRun
	else
		kill -9 $PID
		printStop
		sleepFun
        fi
}
# Tomcat重启函数
restart(){
	getPID
	getPort
	if [[ "${PID}X" == "X" ]]; then
		printNotRunTryStart
		startSingleTomcat
	else
		kill -9 $PID
		printStop
		sleepFun
		startSingleTomcat
	fi
}
# 获取Tomcat状态函数
status(){
	getPID
	if [[ "${PID}X" == "X" ]]; then
		printNotRun
	else
		printRunning
	fi
}

# 判断命令行第二个参数
case ${START_STOP} in
    start   )
        start
        ;;
    stop    )
        stop
        ;;
    restart )
        restart
        ;;
    status  )
        status
        ;;
    *       )
        usage
        ;;
esac

