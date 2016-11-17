var nami = require('../../nami/index.js')

Page({
  data: {
    motto: 'Hello World',
    userInfo: {}
  },

  onLoad: function () {
    console.log('onLoad')
    var that = this

    //NAMI登录
    nami.login(() => {
      //调用接口前需先login
      nami.getUserInfo(function (userInfo) {
        that.setData({
          userInfo: userInfo
        })
      });
    });

  }
})
