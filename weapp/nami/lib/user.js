var config = require('../config')
var constant = require('./constant')

const LOGIN_URL = `${config.host}/login.nami`;//NAMI登录服务
const UNION_ID_URL = `${config.host}/unionid.nami`;//获取unionid并保存在服务端

/**
 * 登录
 */
var login = callback => {
    var namiKey = wx.getStorageSync(constant.NAMI_SESSION_KEY);
    if (namiKey) {
        wx.checkSession({
            success: function () {
                //登录态未过期
                //do nothing
                console.log("已登录");
                storeUserInfo(callback);
            },
            fail: function () {
                remoteLogin(callback)
            }
        })
    } else {
        remoteLogin(callback)
    }
}

/**
 * 服务端请求登录
 */
var remoteLogin = callback => {
    //调用登录接口
    wx.login({
        success: function (loginRes) {
            console.log("登录获取code", loginRes);
            wx.request({
                url: LOGIN_URL,
                data: {
                    code: loginRes.code
                },
                complete: function (res) {
                    if (res.statusCode != 200) {//失败
                        console.error("登陆失败", res);
                        var data = res.data || { msg: '无法请求服务器' };
                        wx.showModal({
                            title: '提示',
                            content: data.msg,
                            showCancel: false
                        })
                    } else {//成功
                        console.log("登录成功", res);
                        wx.setStorage({
                            key: constant.NAMI_SESSION_KEY,
                            data: res.data.key
                        })
                        storeUserInfo(callback);
                    }
                }
            })
        }
    })
}

/**
 * 获取用户资料并保存
 */
var storeUserInfo = callback => {
    wx.getUserInfo({
        success: function (res) {
            console.log("获取用户资料", res);
            getApp().userInfo = res.userInfo;
            typeof callback == "function" && callback(res.userInfo);
        },
        fail: function (res) {
            console.log('fail:', res);
            wx.showModal({
                title: '提示',
                content: '用户未登录,无法获取数据',
                showCancel: false
            })
        },
        complete: function (res) {
            console.log('complete:', res);
        }
    })
}

/**
 * 获取用户信息
 */
var getUserInfo = callback => {
    var userInfo = getApp().userInfo;
    if (userInfo) {
        typeof callback == "function" && callback(userInfo);
    } else {
        storeUserInfo(callback);
    }
}

module.exports = {
    login: login,
    getUserInfo: getUserInfo
}