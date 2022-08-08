# Meiqia IM for Android

## 安装与运行
下载源码，然后运行即可

## 替换资源

### 替换应用名称

更改 app/src/main/res/values/strings.xml 中的 app_name 对应名称即可

### 替换应用图标

更改 app/src/main/res/ 文件下，所有 mipmap 中对应的图标即可

### 替换导航中发现页的 url

更改 app/src/build.gradle 中的 buildConfigField 对应 NAV_TAB_URL 的内容即可

### 离线推送

目前支持对接五家厂商离线推送，分别是：小米、华为、魅族、OPPO、VIVO。如果需要集成离线推送功能，请到各个厂商的推送平台上进行注册并新建应用，获取到相关 appid 和 appkey 后，更改 app/src/build.gradle 中对应的配置

### 替换包名

更改 app/src/build.gradle 中的 applicationId 对应包名即可

## 体验 demo

https://www.pgyer.com/oZii