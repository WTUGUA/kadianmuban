# resource-share
彩蛋视频壁纸

## 一、项目说明

### 1.概要

彩蛋视频壁纸(光点视频壁纸)项目

### 2.版本
```
v2.+--->target22旧版本 gradlew aR打包
v3.+--->target26以上，加入模板DIY,VIP付费
```

## 二、3.+多渠道打包方式

### 1.windows: 
```
gradlew channelRelease

gradlew channelDebug -Pchannels=adesk,baidu,huawei

gradlew channelRelease -Pchannels=xiaomi
```
### 2.macOS/linux:
```
./gradlew channelRelease

./gradlew channelDebug -Pchannels=adesk,baidu,huawei

./gradlew channelRelease -Pchannels=xiaomi
```

## 三、应用内获取渠道号

```
String channel = ChannelReaderUtil.getChannel(getApplicationContext());
```

## 四、签名配置
```
'../pk'这个路径下
channels.txt    所有渠道号
key.keystore    签名文件
pk.gradle       签名配置
pk.properties   签名alias pwd配置

MD5:  59:2A:0C:75:5D:C7:D7:25:EC:E1:0C:38:B0:BC:61:85
SHA1: E9:F9:95:66:C9:6D:01:93:BD:7C:B6:98:7C:C3:82:99:5F:FA:DA:96
SHA256: CB:9C:55:4A:B7:1B:63:FB:2B:94:EB:9D:12:63:F6:32:98:84:16:55:9D:75:EF:CE:A1:0D:3C:0E:E6:2A:4F:67

MD5全小写：592a0c755dc7d725ece10c38b0bc6185
MD5全大写：592A0C755DC7D725ECE10C38B0BC6185
```