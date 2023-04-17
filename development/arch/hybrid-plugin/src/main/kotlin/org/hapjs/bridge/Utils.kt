package org.hapjs.bridge

import groovy.json.JsonOutput
import groovy.json.StringEscapeUtils
import javassist.CtClass
import org.json.JSONObject
import java.io.File

// 生成过滤卡片黑名单后的JSONString
@Deprecated(""" 目前暂时废弃使用。
```card.json
    "featureBlacklist": [
      {
        "name": "system.webview"
      },
      {
        "name": "system.canvas"
      },
      {
        "name": "system.sms"
      }
    ],
    "componentBlacklist": [
      {
        "name": "canvas"
      },
      {
        "name": "web"
      },
      {
        "name": "video"
      }
    ]
```
getCardJsonString 用来实现通过card.json配置控制接口拉黑不能使用的需求，但是历史代码的实现存在一定的缺陷。
正常情况下 
getFeatureMetaDataJSONString 删除system.sms接口

getWidgetListJSONString 删除canvas、web

但是通过反编译发现没并没有删除，有坑，所以暂时先关闭该功能

```MetaDataSetImpl.java
    public String getFeatureMetaDataJSONString(boolean z) {
return "[{\"methods\":[{\"mode\":1,\"name\":\"subscribe\"},{\"mode\":1,\"name\":\"unsubscribe\"},{\"mode\":0,\"name\":\"getProvider\"},{\"mode\":0,\"name\":\"off\"},{\"mode\":2,\"name\":\"on\"}],\"name\":\"service.push\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"pay\"},{\"mode\":0,\"name\":\"getProvider\"}],\"name\":\"service.pay\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"instanceMethod\":true,\"name\":\"__onopen\",\"alias\":\"onopen\",\"type\":2},{\"mode\":2,\"instanceMethod\":true,\"name\":\"__onclose\",\"alias\":\"onclose\",\"type\":2},{\"mode\":2,\"instanceMethod\":true,\"name\":\"__onerror\",\"alias\":\"onerror\",\"type\":2},{\"mode\":0,\"name\":\"__init__\"},{\"mode\":1,\"instanceMethod\":true,\"normalize\":0,\"name\":\"send\"},{\"mode\":1,\"instanceMethod\":true,\"name\":\"close\"},{\"mode\":2,\"instanceMethod\":true,\"name\":\"__onmessage\",\"alias\":\"onmessage\",\"type\":2}],\"name\":\"hap.io.MessageChannel\",\"instantiable\":true},{\"methods\":[{\"mode\":1,\"name\":\"recordEvent\"},{\"mode\":0,\"name\":\"setEnv\"},{\"mode\":1,\"name\":\"initEnv\"},{\"mode\":0,\"name\":\"getEnv\"}],\"name\":\"nearme.stats\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"recordCountEvent\"},{\"mode\":0,\"name\":\"getProvider\"},{\"mode\":1,\"name\":\"recordCalculateEvent\"}],\"name\":\"service.stats\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"isLogin\"},{\"mode\":1,\"name\":\"getEncryptedID\"},{\"mode\":1,\"name\":\"getProfile\"},{\"mode\":0,\"name\":\"getProvider\"},{\"mode\":1,\"name\":\"getPhoneNumber\"},{\"mode\":1,\"name\":\"authorize\"}],\"name\":\"service.account\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"reLogin\"},{\"mode\":1,\"name\":\"getToken\"},{\"mode\":1,\"name\":\"login\"}],\"name\":\"service.oppoinneraccount\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"play\"},{\"mode\":1,\"name\":\"cancel\"},{\"mode\":1,\"name\":\"onfinish\"},{\"mode\":1,\"name\":\"reverse\"},{\"mode\":1,\"name\":\"pause\"},{\"mode\":1,\"name\":\"oncancel\"},{\"mode\":0,\"name\":\"enable\"},{\"mode\":1,\"name\":\"finish\"},{\"mode\":0,\"name\":\"getFinished\"},{\"mode\":1,\"name\":\"setStartTime\"},{\"mode\":0,\"name\":\"getStartTime\"},{\"mode\":0,\"name\":\"getCurrentTime\"},{\"mode\":0,\"name\":\"getPlayState\"},{\"mode\":0,\"name\":\"getPending\"},{\"mode\":0,\"name\":\"getReady\"}],\"name\":\"system.animation\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"setMediaValue\"},{\"mode\":1,\"name\":\"getMediaValue\"}],\"name\":\"system.volume\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"__onregistercallback\",\"alias\":\"onregistercallback\",\"type\":2},{\"mode\":1,\"name\":\"send\"}],\"name\":\"system.hostconnection\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getProvider\"},{\"mode\":1,\"name\":\"setAlarm\"}],\"name\":\"system.alarm\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"scan\"}],\"name\":\"system.barcode\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getStatus\"}],\"name\":\"system.battery\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"__onscanned\",\"alias\":\"onscanned\",\"type\":2},{\"mode\":1,\"name\":\"scan\"},{\"mode\":1,\"name\":\"getConnectedWifi\"},{\"mode\":2,\"name\":\"__onstatechanged\",\"alias\":\"onstatechanged\",\"type\":2},{\"mode\":1,\"name\":\"connect\"}],\"name\":\"system.wifi\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"share\"}],\"name\":\"system.share\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"pickVideos\"},{\"mode\":1,\"name\":\"takeVideo\"},{\"mode\":1,\"name\":\"pickFiles\"},{\"mode\":1,\"name\":\"pickVideo\"},{\"mode\":1,\"name\":\"saveToPhotosAlbum\"},{\"mode\":1,\"name\":\"pickImages\"},{\"mode\":1,\"name\":\"pickImage\"},{\"mode\":1,\"name\":\"setRingtone\"},{\"mode\":1,\"name\":\"takePhoto\"},{\"mode\":1,\"name\":\"pickFile\"},{\"mode\":1,\"name\":\"getRingtone\"},{\"mode\":1,\"name\":\"previewImage\"}],\"name\":\"system.media\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getSimOperators\"},{\"mode\":1,\"name\":\"getType\"},{\"mode\":2,\"name\":\"subscribe\"},{\"mode\":0,\"name\":\"unsubscribe\"}],\"name\":\"system.network\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"onDownloadComplete\"},{\"mode\":1,\"name\":\"download\"},{\"mode\":1,\"name\":\"upload\"}],\"name\":\"system.request\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"showContextMenu\"},{\"mode\":1,\"name\":\"showDialog\"},{\"mode\":0,\"name\":\"hideLoading\"},{\"mode\":0,\"name\":\"showToast\"},{\"mode\":0,\"name\":\"showLoading\"}],\"name\":\"system.prompt\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getKeyguardLockedStatus\"},{\"mode\":1,\"name\":\"requestDismissKeyguard\"}],\"name\":\"system.keyguard\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"pick\"},{\"mode\":1,\"name\":\"queryNumber\"},{\"mode\":1,\"name\":\"list\"}],\"name\":\"system.contact\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"normalize\":0,\"name\":\"decode\"}],\"name\":\"system.decode\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"decompress\"}],\"name\":\"system.zip\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getDevices\"},{\"mode\":1,\"name\":\"closeBLEConnection\"},{\"mode\":1,\"name\":\"startDevicesDiscovery\"},{\"mode\":2,\"name\":\"__ondevicefound\",\"alias\":\"ondevicefound\",\"type\":2},{\"mode\":1,\"normalize\":0,\"name\":\"writeBLECharacteristicValue\"},{\"mode\":1,\"name\":\"readBLECharacteristicValue\"},{\"mode\":2,\"name\":\"__onadapterstatechange\",\"alias\":\"onadapterstatechange\",\"type\":2},{\"mode\":1,\"name\":\"createBLEConnection\"},{\"mode\":1,\"name\":\"getAdapterState\"},{\"mode\":1,\"name\":\"notifyBLECharacteristicValueChange\"},{\"mode\":1,\"name\":\"stopDevicesDiscovery\"},{\"mode\":1,\"name\":\"getBLEDeviceCharacteristics\"},{\"mode\":1,\"name\":\"openAdapter\"},{\"mode\":1,\"name\":\"getBLEDeviceServices\"},{\"mode\":1,\"name\":\"closeAdapter\"},{\"mode\":1,\"name\":\"getConnectedDevices\"},{\"mode\":2,\"name\":\"__onbleconnectionstatechange\",\"alias\":\"onbleconnectionstatechange\",\"type\":2},{\"mode\":2,\"name\":\"__onblecharacteristicvaluechange\",\"alias\":\"onblecharacteristicvaluechange\",\"type\":2}],\"name\":\"system.bluetooth\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"onClose\",\"multiple\":1},{\"mode\":2,\"name\":\"onError\",\"multiple\":1},{\"mode\":1,\"name\":\"load\"},{\"mode\":0,\"name\":\"offLoad\",\"multiple\":1},{\"mode\":0,\"name\":\"offClose\",\"multiple\":1},{\"mode\":0,\"name\":\"offError\",\"multiple\":1},{\"mode\":1,\"name\":\"show\"},{\"mode\":0,\"name\":\"destroy\"},{\"mode\":2,\"name\":\"onLoad\",\"multiple\":1}],\"name\":\"service.ad.rewardedVideo\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"reportAdClick\"},{\"mode\":2,\"name\":\"onError\",\"multiple\":1},{\"mode\":0,\"name\":\"load\"},{\"mode\":0,\"name\":\"reportAdShow\"},{\"mode\":0,\"name\":\"offLoad\",\"multiple\":1},{\"mode\":0,\"name\":\"offError\",\"multiple\":1},{\"mode\":0,\"name\":\"destroy\"},{\"mode\":2,\"name\":\"onLoad\",\"multiple\":1}],\"name\":\"service.ad.native\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"onError\",\"multiple\":1},{\"mode\":0,\"name\":\"offClose\",\"multiple\":1},{\"mode\":1,\"name\":\"show\"},{\"mode\":0,\"name\":\"destroy\"},{\"mode\":2,\"name\":\"onResize\",\"multiple\":1},{\"mode\":0,\"name\":\"offResize\",\"multiple\":1},{\"mode\":1,\"name\":\"hide\"},{\"mode\":2,\"name\":\"onClose\",\"multiple\":1},{\"mode\":0,\"access\":2,\"subAttrs\":[\"left\",\"top\",\"width\",\"height\"],\"name\":\"__setStyle\",\"alias\":\"style\",\"type\":1},{\"mode\":0,\"access\":1,\"subAttrs\":[\"left\",\"top\",\"width\",\"height\",\"realWidth\",\"realHeight\"],\"name\":\"__getStyle\",\"alias\":\"style\",\"type\":1},{\"mode\":0,\"name\":\"offLoad\",\"multiple\":1},{\"mode\":0,\"name\":\"offError\",\"multiple\":1},{\"mode\":2,\"name\":\"onLoad\",\"multiple\":1}],\"name\":\"service.ad.banner\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"onClose\",\"multiple\":1},{\"mode\":2,\"name\":\"onError\",\"multiple\":1},{\"mode\":0,\"name\":\"load\"},{\"mode\":0,\"name\":\"offLoad\",\"multiple\":1},{\"mode\":0,\"name\":\"offClose\",\"multiple\":1},{\"mode\":0,\"name\":\"offError\",\"multiple\":1},{\"mode\":1,\"name\":\"show\"},{\"mode\":0,\"name\":\"destroy\"},{\"mode\":2,\"name\":\"onLoad\",\"multiple\":1}],\"name\":\"service.ad.interstitial\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"isSpeaking\"},{\"mode\":1,\"name\":\"isLanguageAvailable\"},{\"mode\":0,\"name\":\"stop\"},{\"mode\":1,\"name\":\"textToAudioFile\"},{\"mode\":2,\"name\":\"__onttsstatechange\",\"alias\":\"onttsstatechange\",\"type\":2},{\"mode\":1,\"name\":\"speak\"}],\"name\":\"service.texttoaudio\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"normalize\":0,\"name\":\"fetch\"}],\"name\":\"system.fetch\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"select\"},{\"mode\":1,\"name\":\"insert\"},{\"mode\":1,\"name\":\"update\"},{\"mode\":1,\"name\":\"getTimeFormat\"},{\"mode\":1,\"name\":\"delete\"}],\"name\":\"system.calendar\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getInfo\"},{\"mode\":1,\"name\":\"install\"},{\"mode\":1,\"name\":\"getSignatureDigests\"},{\"mode\":1,\"name\":\"hasInstalled\"},{\"mode\":1,\"name\":\"isEnable\"}],\"name\":\"system.package\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"unregisterCallback\"},{\"mode\":1,\"name\":\"send\"},{\"mode\":2,\"name\":\"registerCallback\"}],\"name\":\"system.localconnection\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"publish\"}],\"name\":\"nearme.media\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"cancel\"},{\"mode\":1,\"name\":\"getInfo\"},{\"mode\":2,\"name\":\"subscribe\",\"multiple\":1},{\"mode\":2,\"name\":\"unsubscribe\",\"multiple\":1},{\"mode\":2,\"name\":\"autoDownload\"},{\"mode\":1,\"name\":\"getSingleStatus\"},{\"mode\":1,\"name\":\"getStatus\"},{\"mode\":0,\"name\":\"support\"},{\"mode\":1,\"name\":\"pause\"}],\"name\":\"nearme.download\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"open\"}],\"name\":\"nearme.browser\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getHeader\"}],\"name\":\"nearme.http\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"install\"},{\"mode\":1,\"name\":\"hasInstalled\"}],\"name\":\"nearme.shortcut\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getDeviceId\"}],\"name\":\"nearme.device\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"openPage\"}],\"name\":\"nearme.navigator\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"request\"},{\"mode\":0,\"name\":\"support\"}],\"name\":\"nearme.router\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"protectedSwitchOn\"},{\"mode\":1,\"name\":\"isAppProtected\"},{\"mode\":1,\"name\":\"verificationPassword\"}],\"name\":\"nearme.appencryption\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"clearDataUsage\"},{\"mode\":1,\"name\":\"getHistoryList\"},{\"mode\":1,\"name\":\"removeHistory\"},{\"mode\":1,\"name\":\"enableUrl\"},{\"mode\":1,\"name\":\"disableUrl\"},{\"mode\":1,\"name\":\"getDataUsage\"},{\"mode\":0,\"name\":\"getHistorySize\"}],\"name\":\"nearme.history\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getNightMode\"},{\"mode\":2,\"name\":\"__onnightmodechange\",\"alias\":\"onnightmodechange\",\"type\":2}],\"name\":\"nearme.theme\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"cancel\"},{\"mode\":1,\"name\":\"download\"},{\"mode\":2,\"name\":\"onProgressUpdate\",\"multiple\":1},{\"mode\":1,\"name\":\"getInfo\"},{\"mode\":0,\"name\":\"offProgressUpdate\",\"multiple\":1},{\"mode\":2,\"name\":\"subscribe\",\"multiple\":1},{\"mode\":2,\"name\":\"unsubscribe\",\"multiple\":1},{\"mode\":2,\"name\":\"autoDownload\"},{\"mode\":1,\"name\":\"getSingleStatus\"},{\"mode\":1,\"name\":\"getStatus\"},{\"mode\":0,\"name\":\"support\"},{\"mode\":1,\"name\":\"pause\"}],\"name\":\"nearme.package\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"geocodeQuery\"},{\"mode\":1,\"name\":\"getLocation\"},{\"mode\":2,\"name\":\"subscribe\"},{\"mode\":0,\"name\":\"unsubscribe\"},{\"mode\":0,\"name\":\"getSupportedCoordTypes\"},{\"mode\":1,\"name\":\"reverseGeocodeQuery\"},{\"mode\":2,\"name\":\"chooseLocation\"},{\"mode\":1,\"name\":\"getLocationType\"},{\"mode\":2,\"name\":\"openLocation\"}],\"name\":\"system.geolocation\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"vibrate\"}],\"name\":\"system.vibrator\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"setKeepScreenOn\"},{\"mode\":1,\"name\":\"getValue\"},{\"mode\":1,\"name\":\"setValue\"},{\"mode\":1,\"name\":\"setMode\"},{\"mode\":1,\"name\":\"getMode\"}],\"name\":\"system.brightness\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"readArrayBuffer\"},{\"mode\":1,\"name\":\"writeText\"},{\"mode\":1,\"name\":\"move\"},{\"mode\":1,\"name\":\"access\"},{\"mode\":1,\"name\":\"get\"},{\"mode\":1,\"name\":\"readText\"},{\"mode\":1,\"name\":\"copy\"},{\"mode\":1,\"name\":\"rmdir\"},{\"mode\":1,\"name\":\"list\"},{\"mode\":1,\"name\":\"delete\"},{\"mode\":1,\"name\":\"mkdir\"},{\"mode\":1,\"normalize\":0,\"name\":\"writeArrayBuffer\"}],\"name\":\"system.file\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"set\"},{\"mode\":1,\"name\":\"get\"},{\"mode\":1,\"name\":\"clear\"},{\"mode\":0,\"access\":1,\"name\":\"__getLength\",\"alias\":\"length\",\"type\":1},{\"mode\":1,\"name\":\"delete\"},{\"mode\":1,\"name\":\"key\"}],\"name\":\"system.storage\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"instanceMethod\":true,\"name\":\"compressVideo\"},{\"mode\":2,\"instanceMethod\":true,\"name\":\"__onprogressupdate\",\"alias\":\"onprogressupdate\",\"type\":2},{\"mode\":1,\"instanceMethod\":true,\"name\":\"abort\"},{\"mode\":1,\"name\":\"getVideoInfo\"},{\"mode\":0,\"name\":\"__init__\"},{\"mode\":1,\"name\":\"getVideoThumbnail\"}],\"name\":\"hap.io.Video\",\"instantiable\":true},{\"methods\":[{\"mode\":1,\"name\":\"back\"},{\"mode\":1,\"name\":\"open\"}],\"name\":\"system.launcher\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"rpkNotificationControl\"},{\"mode\":0,\"access\":1,\"name\":\"__getrpkNotificationPermission\",\"alias\":\"rpkNotificationPermission\",\"type\":1},{\"mode\":1,\"name\":\"appNotificationControl\"},{\"mode\":1,\"name\":\"rpkNotificationPermission\"},{\"mode\":0,\"access\":1,\"name\":\"__getappNotificationPermission\",\"alias\":\"appNotificationPermission\",\"type\":1}],\"name\":\"system.permission\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"rsa\"},{\"mode\":1,\"name\":\"aes\"}],\"name\":\"system.cipher\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getOAID\"},{\"mode\":1,\"name\":\"getInfo\"},{\"mode\":1,\"name\":\"getCpuInfo\"},{\"mode\":1,\"name\":\"getId\"},{\"mode\":0,\"access\":1,\"name\":\"__getPlatform\",\"alias\":\"platform\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getHost\",\"alias\":\"host\",\"type\":1},{\"mode\":1,\"name\":\"getDeviceId\"},{\"mode\":1,\"name\":\"getSerial\"},{\"mode\":1,\"name\":\"getAvailableStorage\"},{\"mode\":1,\"name\":\"getTotalStorage\"},{\"mode\":0,\"access\":1,\"name\":\"__getAllowTrackOAID\",\"alias\":\"allowTrackOAID\",\"type\":1},{\"mode\":1,\"name\":\"getUserId\"},{\"mode\":1,\"name\":\"getAdvertisingId\"}],\"name\":\"system.device\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"onHeadersReceived\",\"multiple\":1},{\"mode\":3,\"name\":\"uploadFile\"},{\"mode\":2,\"name\":\"onProgressUpdate\",\"multiple\":1},{\"mode\":0,\"name\":\"offProgressUpdate\",\"multiple\":1},{\"mode\":1,\"name\":\"abort\"},{\"mode\":0,\"name\":\"offHeadersReceived\",\"multiple\":1}],\"name\":\"system.uploadtask\",\"instantiable\":false},{\"methods\":[{\"mode\":3,\"name\":\"downloadFile\"},{\"mode\":2,\"name\":\"onHeadersReceived\",\"multiple\":1},{\"mode\":2,\"name\":\"onProgressUpdate\",\"multiple\":1},{\"mode\":0,\"name\":\"offProgressUpdate\",\"multiple\":1},{\"mode\":0,\"name\":\"abort\"},{\"mode\":0,\"name\":\"offHeadersReceived\",\"multiple\":1}],\"name\":\"system.downloadtask\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"onHeadersReceived\",\"multiple\":1},{\"mode\":3,\"name\":\"request\"},{\"mode\":1,\"name\":\"abort\"},{\"mode\":0,\"name\":\"offHeadersReceived\",\"multiple\":1}],\"name\":\"system.requesttask\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"set\"},{\"mode\":1,\"name\":\"get\"}],\"name\":\"system.clipboard\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"stop\"},{\"mode\":1,\"name\":\"start\"}],\"name\":\"system.record\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"send\"},{\"mode\":1,\"name\":\"readSafely\"}],\"name\":\"system.sms\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"normalize\":0,\"name\":\"onDiscovered\"},{\"mode\":0,\"name\":\"getNFCAdapter\"},{\"mode\":0,\"name\":\"getNfcV\"},{\"mode\":1,\"normalize\":0,\"name\":\"getAtqa\"},{\"mode\":0,\"name\":\"offDiscovered\"},{\"mode\":1,\"name\":\"getMaxTransceiveLength\"},{\"mode\":1,\"name\":\"isConnected\"},{\"mode\":1,\"name\":\"startDiscovery\"},{\"mode\":0,\"name\":\"getIsoDep\"},{\"mode\":0,\"name\":\"getNfcA\"},{\"mode\":1,\"normalize\":0,\"name\":\"transceive\"},{\"mode\":0,\"name\":\"getNdef\"},{\"mode\":0,\"name\":\"getNfcB\"},{\"mode\":0,\"name\":\"getMifareUltralight\"},{\"mode\":1,\"name\":\"getSak\"},{\"mode\":1,\"name\":\"stopDiscovery\"},{\"mode\":0,\"name\":\"getMifareClassic\"},{\"mode\":1,\"name\":\"setTimeout\"},{\"mode\":0,\"name\":\"getNfcF\"},{\"mode\":1,\"normalize\":0,\"name\":\"getHistoricalBytes\"},{\"mode\":2,\"name\":\"writeNdefMessage\"},{\"mode\":1,\"name\":\"close\"},{\"mode\":1,\"name\":\"connect\"}],\"name\":\"system.nfc\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"unsubscribeProximity\"},{\"mode\":2,\"name\":\"subscribeProximity\"},{\"mode\":2,\"name\":\"subscribeLight\"},{\"mode\":2,\"name\":\"subscribeAccelerometer\"},{\"mode\":0,\"name\":\"unsubscribeAccelerometer\"},{\"mode\":2,\"name\":\"subscribeStepCounter\"},{\"mode\":0,\"name\":\"unsubscribeStepCounter\"},{\"mode\":2,\"name\":\"subscribeCompass\"},{\"mode\":0,\"name\":\"unsubscribeLight\"},{\"mode\":0,\"name\":\"unsubscribeCompass\"}],\"name\":\"system.sensor\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"offUserCaptureScreen\"},{\"mode\":2,\"name\":\"onUserCaptureScreen\"}],\"name\":\"system.screenshot\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getTelecomInfo\"}],\"name\":\"system.telecom\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getString\"}],\"name\":\"android.settings\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"show\"}],\"name\":\"system.notification\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"__onopen\",\"alias\":\"onopen\",\"type\":2},{\"mode\":2,\"name\":\"__onclose\",\"alias\":\"onclose\",\"type\":2},{\"mode\":2,\"name\":\"__onerror\",\"alias\":\"onerror\",\"type\":2},{\"mode\":1,\"normalize\":0,\"name\":\"send\"},{\"mode\":1,\"name\":\"close\"},{\"mode\":2,\"name\":\"__onmessage\",\"alias\":\"onmessage\",\"type\":2}],\"name\":\"system.websocket\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"create\"}],\"name\":\"system.websocketfactory\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"applyOperations\"},{\"mode\":1,\"name\":\"getExifAttributes\"},{\"mode\":1,\"name\":\"getImageInfo\"},{\"mode\":1,\"name\":\"getInfo\"},{\"mode\":1,\"name\":\"compress\"},{\"mode\":1,\"name\":\"edit\"},{\"mode\":1,\"name\":\"setExifAttributes\"},{\"mode\":1,\"name\":\"compressImage\"},{\"mode\":1,\"name\":\"editImage\"}],\"name\":\"system.image\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getLastWeekSteps\"},{\"mode\":1,\"name\":\"getTodaySteps\"},{\"mode\":1,\"name\":\"hasStepsOfDay\"}],\"name\":\"service.health\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"createQRImage\"}],\"name\":\"nearme.image\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getBadgeCount\"},{\"mode\":1,\"name\":\"getBadgeMode\"},{\"mode\":1,\"name\":\"setBadgeCount\"}],\"name\":\"nearme.badge\",\"instantiable\":false},{\"methods\":[{\"mode\":2,\"name\":\"__onvoicerequest\",\"alias\":\"onvoicerequest\",\"type\":2}],\"name\":\"nearme.voice\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"setFreeze\"}],\"name\":\"system.freeze\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"getVersion\"},{\"mode\":0,\"name\":\"getType\"},{\"mode\":1,\"name\":\"pay\"}],\"name\":\"service.alipay\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"set\"},{\"mode\":1,\"name\":\"grantPermission\"},{\"mode\":1,\"name\":\"get\"},{\"mode\":1,\"name\":\"revokePermission\"},{\"mode\":1,\"name\":\"clear\"},{\"mode\":1,\"name\":\"remove\"}],\"name\":\"service.exchange\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getType\"},{\"mode\":1,\"name\":\"authorize\"}],\"name\":\"service.wxaccount\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getType\"},{\"mode\":1,\"name\":\"authorize\"}],\"name\":\"service.qqaccount\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"setKeyWords\"},{\"mode\":1,\"name\":\"setSplashAdSwitch\"},{\"mode\":0,\"name\":\"createBannerAd\"},{\"mode\":0,\"name\":\"createInterstitialAd\"},{\"mode\":0,\"name\":\"getProvider\"},{\"mode\":0,\"name\":\"createRewardedVideoAd\"},{\"mode\":1,\"name\":\"splashAdSwitch\"},{\"mode\":0,\"name\":\"createNativeAd\"},{\"mode\":1,\"name\":\"preloadAd\"}],\"name\":\"service.ad\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getType\"},{\"mode\":1,\"name\":\"authorize\"}],\"name\":\"service.wbaccount\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getProvider\"},{\"mode\":1,\"name\":\"share\"},{\"mode\":1,\"name\":\"getAvailablePlatforms\"}],\"name\":\"service.share\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"play\"},{\"mode\":0,\"access\":2,\"name\":\"__setSrc\",\"alias\":\"src\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getDuration\",\"alias\":\"duration\",\"type\":1},{\"mode\":0,\"access\":2,\"name\":\"__setMuted\",\"alias\":\"muted\",\"type\":1},{\"mode\":2,\"name\":\"__onpause\",\"alias\":\"onpause\",\"type\":2},{\"mode\":2,\"name\":\"__onloadeddata\",\"alias\":\"onloadeddata\",\"type\":2},{\"mode\":0,\"access\":1,\"name\":\"__getSrc\",\"alias\":\"src\",\"type\":1},{\"mode\":0,\"access\":2,\"name\":\"__setLoop\",\"alias\":\"loop\",\"type\":1},{\"mode\":0,\"access\":2,\"name\":\"__setArtist\",\"alias\":\"artist\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getAutoplay\",\"alias\":\"autoplay\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getCover\",\"alias\":\"cover\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getLoop\",\"alias\":\"loop\",\"type\":1},{\"mode\":0,\"access\":2,\"name\":\"__setTitle\",\"alias\":\"title\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getVolume\",\"alias\":\"volume\",\"type\":1},{\"mode\":2,\"name\":\"__ontimeupdate\",\"alias\":\"ontimeupdate\",\"type\":2},{\"mode\":0,\"access\":2,\"name\":\"__setStreamType\",\"alias\":\"streamType\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getTitle\",\"alias\":\"title\",\"type\":1},{\"mode\":2,\"name\":\"__onnext\",\"alias\":\"onnext\",\"type\":2},{\"mode\":0,\"access\":2,\"name\":\"__setCurrentTime\",\"alias\":\"currentTime\",\"type\":1},{\"mode\":2,\"name\":\"__onended\",\"alias\":\"onended\",\"type\":2},{\"mode\":1,\"name\":\"getPlayState\"},{\"mode\":2,\"name\":\"__onplay\",\"alias\":\"onplay\",\"type\":2},{\"mode\":0,\"access\":1,\"name\":\"__getArtist\",\"alias\":\"artist\",\"type\":1},{\"mode\":2,\"name\":\"__onprevious\",\"alias\":\"onprevious\",\"type\":2},{\"mode\":0,\"access\":2,\"name\":\"__setAutoplay\",\"alias\":\"autoplay\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getCurrentTime\",\"alias\":\"currentTime\",\"type\":1},{\"mode\":0,\"access\":2,\"name\":\"__setVolume\",\"alias\":\"volume\",\"type\":1},{\"mode\":2,\"name\":\"__onerror\",\"alias\":\"onerror\",\"type\":2},{\"mode\":0,\"access\":2,\"name\":\"__setCover\",\"alias\":\"cover\",\"type\":1},{\"mode\":2,\"name\":\"__onstop\",\"alias\":\"onstop\",\"type\":2},{\"mode\":1,\"name\":\"pause\"},{\"mode\":0,\"access\":2,\"name\":\"__setNotificationVisible\",\"alias\":\"notificationVisible\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getMuted\",\"alias\":\"muted\",\"type\":1},{\"mode\":1,\"name\":\"stop\"},{\"mode\":0,\"access\":1,\"name\":\"__getStreamType\",\"alias\":\"streamType\",\"type\":1},{\"mode\":0,\"access\":1,\"name\":\"__getNotificationVisible\",\"alias\":\"notificationVisible\",\"type\":1},{\"mode\":2,\"name\":\"__ondurationchange\",\"alias\":\"ondurationchange\",\"type\":2}],\"name\":\"system.audio\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"install\"},{\"mode\":0,\"access\":1,\"name\":\"__getSystemPromptEnabled\",\"alias\":\"systemPromptEnabled\",\"type\":1},{\"mode\":1,\"name\":\"hasInstalled\"},{\"mode\":1,\"name\":\"onInstalled\"},{\"mode\":0,\"access\":2,\"name\":\"__setSystemPromptEnabled\",\"alias\":\"systemPromptEnabled\",\"type\":1}],\"name\":\"system.shortcut\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getType\"},{\"mode\":1,\"name\":\"pay\"}],\"name\":\"service.wxpay\",\"instantiable\":false}]";
}
 *     public String getModuleMetaDataJSONString(boolean z) {
return "[{\"methods\":[{\"mode\":0,\"name\":\"getThemeMode\"},{\"mode\":0,\"name\":\"getLocale\"},{\"mode\":0,\"name\":\"setGrayMode\"},{\"mode\":0,\"name\":\"setLocale\"}],\"name\":\"system.configuration\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"stop\"},{\"mode\":0,\"name\":\"start\"}],\"name\":\"system.resident\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getState\"},{\"mode\":0,\"name\":\"replace\"},{\"mode\":0,\"name\":\"clear\"},{\"mode\":0,\"name\":\"back\"},{\"mode\":0,\"name\":\"getLength\"},{\"mode\":0,\"name\":\"getPages\"},{\"mode\":0,\"name\":\"push\"}],\"name\":\"system.router\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"loadUrl\"}],\"name\":\"system.webview\",\"instantiable\":false},{\"methods\":[{\"mode\":1,\"name\":\"add\"},{\"mode\":1,\"name\":\"checkState\"}],\"name\":\"system.card\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"exit\"},{\"mode\":0,\"name\":\"getInfo\"},{\"mode\":1,\"name\":\"createQuickAppQRCode\"}],\"name\":\"system.app\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"getComputedStyle\"},{\"mode\":0,\"name\":\"getComponent\"},{\"mode\":0,\"name\":\"getBoundingRect\"},{\"mode\":0,\"name\":\"getComputedAttr\"}],\"name\":\"system.model\",\"instantiable\":false},{\"methods\":[{\"mode\":0,\"name\":\"finishPage\"},{\"mode\":0,\"name\":\"setMenubarTips\"},{\"mode\":0,\"name\":\"getMenuBarBoundingRect\"},{\"mode\":0,\"name\":\"setMenubarData\"},{\"mode\":0,\"name\":\"getMenuBarRect\"}],\"name\":\"system.page\",\"instantiable\":false}]";
}

public String getWidgetMetaDataJSONString(boolean z) {
return "[{\"methods\":[{\"mode\":1,\"name\":\"preloadImage\"},{\"mode\":0,\"name\":\"canvasNative2D\"},{\"mode\":0,\"name\":\"enable\"},{\"mode\":0,\"name\":\"canvasNative2DSync\"},{\"mode\":0,\"name\":\"getContext\"}],\"name\":\"system.canvas\",\"instantiable\":false}]";
}

 *     public String getWidgetListJSONString(boolean z) {
return "[{\"name\":\"ad\"},{\"name\":\"ad-clickable-area\"},{\"name\":\"mediaad\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"rating\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"select\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"a\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"slider\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"refresh\"},{\"methods\":[\"toTempFilePath\",\"getBoundingClientRect\",\"focus\"],\"name\":\"canvas\"},{\"methods\":[\"getOBusinessState\",\"success\",\"cancel\",\"fail\",\"onclick\"],\"name\":\"obusiness-button\"},{\"methods\":[\"scrollTo\",\"scrollBy\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"list\"},{\"methods\":[\"animate\",\"toTempFilePath\",\"focus\",\"getBoundingClientRect\"],\"name\":\"list-item\"},{\"types\":[\"text\"],\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"text\"},{\"methods\":[\"pause\",\"resume\",\"start\",\"stop\",\"animate\",\"getBoundingClientRect\",\"focus\",\"toTempFilePath\"],\"name\":\"marquee\"},{\"types\":[\"html\"],\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"text\"},{\"methods\":[\"addContent\",\"getBoundingClientRect\",\"animate\",\"toTempFilePath\",\"focus\"],\"name\":\"richtext\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"section-header\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\",\"scrollTo\"],\"name\":\"section-list\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"section-item\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\",\"expand\",\"scrollTo\"],\"name\":\"section-group\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"tabs\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"tab-content\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"tab-bar\"},{\"methods\":[\"focus\"],\"name\":\"option\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"refresh-footer\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"refresh-header\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\",\"startPullDownRefresh\",\"startPullUpRefresh\",\"stopPullDownRefresh\",\"stopPullUpRefresh\"],\"name\":\"refresh2\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"div\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"open\",\"close\",\"hideSecondaryConfirm\"],\"name\":\"slide-view\"},{\"name\":\"bounce\"},{\"methods\":[\"focus\"],\"name\":\"span\"},{\"methods\":[\"getBoundingClientRect\",\"focus\",\"toTempFilePath\"],\"name\":\"custommarker\"},{\"methods\":[\"getCenterLocation\",\"translateMarker\",\"moveToMyLocation\",\"includePoints\",\"getCoordType\",\"convertCoord\",\"getRegion\",\"getScale\",\"getSupportedCoordTypes\",\"setIndoorEnable\",\"switchIndoorFloor\",\"setCompassPosition\",\"setScalePosition\",\"setZoomPosition\",\"setMaxAndMinScaleLevel\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"map\"},{\"methods\":[\"animate\",\"requestFullscreen\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"stack\"},{\"methods\":[\"swipeTo\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"swiper\"},{\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\"],\"name\":\"shortcut-button\"},{\"methods\":[\"reload\",\"forward\",\"back\",\"canForward\",\"canBack\",\"postMessage\",\"isSupportWebRTC\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"web\"},{\"methods\":[\"animate\",\"toTempFilePath\",\"focus\",\"getBoundingClientRect\"],\"name\":\"popup\"},{\"types\":[\"radio\"],\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\"],\"name\":\"input\"},{\"types\":[\"eventbutton\"],\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\"],\"name\":\"input\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"switch\"},{\"types\":[\"checkbox\"],\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\"],\"name\":\"input\"},{\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\"],\"name\":\"label\"},{\"types\":[\"text\",\"date\",\"time\",\"email\",\"number\",\"password\",\"tel\"],\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"select\",\"setSelectionRange\",\"getSelectionRange\"],\"name\":\"input\"},{\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"select\",\"setSelectionRange\",\"getSelectionRange\"],\"name\":\"textarea\"},{\"types\":[\"button\"],\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\"],\"name\":\"input\"},{\"types\":[\"horizontal\"],\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"progress\"},{\"types\":[\"circular\"],\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"progress\"},{\"methods\":[\"openDrawer\",\"closeDrawer\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"drawer\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\"],\"name\":\"drawer-navigation\"},{\"types\":[\"time\"],\"methods\":[\"show\",\"animate\",\"focus\",\"toTempFilePath\",\"getBoundingClientRect\"],\"name\":\"picker\"},{\"types\":[\"date\"],\"methods\":[\"show\",\"animate\",\"focus\",\"toTempFilePath\",\"getBoundingClientRect\"],\"name\":\"picker\"},{\"types\":[\"text\"],\"methods\":[\"show\",\"animate\",\"focus\",\"toTempFilePath\",\"getBoundingClientRect\"],\"name\":\"picker\"},{\"types\":[\"multi-text\"],\"methods\":[\"show\",\"animate\",\"focus\",\"toTempFilePath\",\"getBoundingClientRect\"],\"name\":\"picker\"},{\"methods\":[\"animate\",\"getBoundingClientRect\",\"toTempFilePath\",\"focus\",\"startAnimation\",\"stopAnimation\"],\"name\":\"image\"},{\"methods\":[\"focus\",\"animate\",\"getBoundingClientRect\",\"toTempFilePath\"],\"name\":\"share-button\"}]";
}
```
""")
fun File?.getCardJsonString(key: String?, jsonlist: List<Map<String, Any>>): String? {
    if (this == null) {
        P.warn("不需要 cardJsonFile}")
        return null
    }
    val array = jsonlist.toMutableList()
    if (!exists()) {
        P.warn("cardJsonFile文件不存在 ${absolutePath}  ${key}")
        return null
    }
    val cardBlackListJSONObject = JSONObject(readText())
    val cardBlackListJSONArray =
        cardBlackListJSONObject.optJSONObject("policy").optJSONArray(key)
    if (cardBlackListJSONArray == null) {
//        P.warn("cardJsonFile文件 ${key}字段不存在")
        return null
    }
    //    "featureBlacklist": [
    //      {
    //        "name": "system.webview"
    //      },
    //      {
    //        "name": "system.canvas"
    //      },
    //      {
    //        "name": "system.sms"
    //      }
    //    ],
    for (i in 0 until cardBlackListJSONArray.length()) {
        val blackInfo = cardBlackListJSONArray.getJSONObject(i)

        val blackInterface =
            array.filter { it["name"] == blackInfo.optString("name").trim() }.toList().lastOrNull()
                ?: continue
        val blackMethods = blackInfo.optJSONArray("methods")
        //删除具体的某个方法
        if (blackMethods != null) {
            val waitingForRemovingMethods = mutableListOf<Map<String, Any>>()
            val methods = blackInterface["methods"] as? MutableList<Map<String, Any>>
            methods?.forEach { method ->
                if (blackMethods.contains(method["name"])) {
                    waitingForRemovingMethods.add(method)
                }
            }
            methods?.removeAll(waitingForRemovingMethods)
            P.warn("2. 剔除方法 ${waitingForRemovingMethods.joinToString(",")}")
        } else {
            //整个接口删除
            array.remove(blackInterface)
            P.warn("2. 剔除整个接口 ${blackInterface["name"]}")
        }
    }
    return JsonOutput.toJson(array)
}


/**
 * extensions 、widget list数据被jsonify之后插入到getXxxMetaDataJSONString方法
 */
fun CtClass.injectMetaDataJSONString(
    methodName: String,
    cardJsonString: String,
    qaJsonString: String
) {
    getDeclaredMethod(methodName)
        .insertAt(1, buildString {
            append("String result = \"\";")
            append("if ($1) {")
            append("    result = String.valueOf(\"${StringEscapeUtils.escapeJava(cardJsonString)}\");")
            append("} else {")
            append("    result = String.valueOf(\"${StringEscapeUtils.escapeJava(qaJsonString)}\");")
            append("}")
            append("return result;")
        })
}

/**
 * extensions 、widget list数据插入到initXxxMetaData方法
 */
inline fun <reified T> CtClass.injectMetaData(a: List<T>?, methodName: String) {
    if (a.isNullOrEmpty()) return
    getDeclaredMethod(methodName)
        .insertAfter(buildString {
            if (a.first() is Extension) {
                appendln("org.hapjs.bridge.ExtensionMetaData extension;")
                a.filterIsInstance<Extension>().forEach { extension ->
                    appendln("extension = new org.hapjs.bridge.ExtensionMetaData(\"${extension.name}\", \"${extension.classname}\");")
                    extension.methods.forEach { method ->
                        val alias = if (method.alias.isEmpty()) "\"\"" else "\"${method.alias}\""
                        val permissions =
                            if (method.permissions.isEmpty()) "null" else "new String[] {${
                                method.permissions.joinToString(",") { "\"$it\"" }
                            }}"
                        val subAttrs =
                            if (method.subAttrs.isEmpty()) "null" else "new String[] {${
                                method.subAttrs.joinToString(",") { "\"$it\"" }
                            }}"
                        appendln(
                            "extension.addMethod(\"${method.name}\", ${method.isInstanceMethod}, " +
                                    "org.hapjs.bridge.Extension.Mode.${method.mode.name}, org.hapjs.bridge.Extension.Type.${method.type.name}, " +
                                    "org.hapjs.bridge.Extension.Access.${method.access.name}, org.hapjs.bridge.Extension.Normalize.${method.normalize.name}, " +
                                    "org.hapjs.bridge.Extension.Multiple.${method.multiple.name}, ${alias}, " +
                                    "$permissions,$subAttrs);"
                        )
                    }
                    appendln("extension.validate();")
                    appendln("$1.put(\"${extension.name}\", extension);")
                }
            } else if (a.first() is Widget) {
                appendln("org.hapjs.bridge.Widget widget;")
                a.filterIsInstance<Widget>().forEach { widget ->
                    appendln("widget = new org.hapjs.bridge.Widget(\"${widget.name}\", ${widget.classname}.class);")
                    widget.types.forEach { type ->
                        appendln("widget.addType(\"${type.name}\", \"${type.isDefault}\");")
                    }
                    widget.methods.forEach { method ->
                        appendln("widget.addMethod(\"${method}\");")
                    }
                    appendln("$1.add(widget);")
                }
            }
        })
}

fun CtClass.injectResidentSet(
    injectCode: String?,
    methodName: String
) {
    if (injectCode.isNullOrEmpty()) return
    getDeclaredMethod(methodName)
        .insertAfter(injectCode)
}

fun CtClass?.getSuperclasses(): List<String> {
    val allSuperClassName = mutableListOf<String>()
    var sc = this?.superclass
    while (sc?.name?.isNotEmpty() == true) {
        allSuperClassName.add(sc.name)
        sc = sc.superclass
    }
    return allSuperClassName
}

/**
 * 一对多的数据结构
 */
class SonsMap<K, V> : MutableMap<K, MutableList<V>> {
    companion object {
        fun <K, V> of(): SonsMap<K, V> {
            return SonsMap()
        }
    }

    private val g = mutableMapOf<K, MutableList<V>>()

    override val entries: MutableSet<MutableMap.MutableEntry<K, MutableList<V>>>
        get() = g.entries
    override val keys: MutableSet<K>
        get() = g.keys
    override val size: Int
        get() = g.size
    override val values: MutableCollection<MutableList<V>>
        get() = g.values

    override fun containsKey(key: K) = g.containsKey(key)
    override fun get(key: K) = g[key]
    override fun isEmpty() = g.isEmpty()
    override fun clear() = g.clear()
    override fun remove(key: K) = g.remove(key)
    override fun containsValue(value: MutableList<V>) = g.containsValue(value)
    fun putSon(key: K, value: V): V? {
        var list = g[key]
        if (list == null) {
            list = mutableListOf()
        }
        list.add(value)
        g[key] = list
        return g[key]?.last()
    }

    override fun put(key: K, value: MutableList<V>) = g.put(key, value)
    override fun putAll(from: Map<out K, MutableList<V>>) = g.putAll(from)

}
