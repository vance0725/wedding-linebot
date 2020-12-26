# 婚禮LineBot

先前參加朋友的婚禮，看到他的問卷是用linebot寫的，覺得相當酷炫！ \
但等到自己的婚禮時，卻沒有使用linebot發放問卷XD \
會有這個linebot，主要是覺得單純婚紗輪播好像有點無聊~ \
~~當然還有自己想要多拿到賓客拍的照片~~ 哈哈哈 \
以下是婚禮當天有用到的功能:

- user可以用line上傳照片&文字
- 輪播user上傳的照片&文字
- 簡易文字篩選功能
- 可篩選照片(後來註解掉了)
- 文字/照片抽獎
- google photo瀏覽全部上傳照片
- google map路徑規劃(寫好了，未使用)
- 內定圖片中獎名單(需要賓客先上傳照片，才能內定)

# ToolKit
- Java Springboot
- Maven
- MySQL

# 事前準備

1. 到[LINE Developers](https://developers.line.biz/zh-hant/)建立一個line官方帳號
2. 一個google帳號 \
需開通GCP VM \
需開通Google Oauth \
需開通Google Photo API \
(詳細可以參考[這篇](https://www.wfublog.com/2019/12/google-apps-script-google-photo-api-upload.html) OR [這篇](https://salu099.github.io/blog/2018/06/csharp-google-photos-api/))
3. VM需安裝[imagemagick](https://imagemagick.org/)

# 程式開發說明

大概說一下中間會遇到那些坑
1.  用line上傳照片&文字
line有提供[maven SDK](https://mvnrepository.com/artifact/com.linecorp.bot/line-bot-spring-boot/3.3.1)，可以辨別訊息的類型(文字、圖片、影片...等)，非常方便

2.  上傳照片/影片到google photo
可以用API取得google photo上的照片，但url時效只有1小時！另外，若不是使用取得照片/影片的API，每天最高只能request 10000次(如果是因爲url過期更新的話，也包含在內哦)！爲了避免達到使用上限，所以後來圖片都放在GCP上了~哈哈哈，這樣只需要提供google相簿連結給user看就好XD

3. Google相簿只能用API建立，直接用UI建立的話，就不能透過API控制了
~~但這部分我沒寫...直接用postman解決這一切~~

4. 個人覺得Google Photo的Java API有看沒有懂，最後都採用restful api的方式呼叫，也比較直覺

5. imagemagick java套件 \
其實可以透過im4java的套件，來呼叫linux主機上的imagemagick指令。\
但因爲當下找不到像下面固定高度的指令，只好用Java Process呼叫了...@@

    convert -quality 80 -resize x1080 original/test.jpg test-c.jpg

6. 需先寫好shell，便於執行imagemagick \
shell可參考[compressPhoto.sh](https://github.com/vance0725/wedding-linebot/blob/main/compressPhoto.sh)

7. 文字篩選功能 \
詞庫是參考[這裡](https://github.com/lyenliang/Profanity-Filter)

8. 程式入口點 \
MessageServiceImpl.java -> line訊息 \
MainController.java -> Web UI / API

9. 沒有登入機制 \
~~我就懶👍~~ \
結婚前一個月才想到做這個，又覺得只有自己會用，就沒做了哈哈哈

10. 文字抽獎是抽全部的人，照片抽獎才有內定名單 \
[randMarquee.html](https://github.com/vance0725/wedding-linebot/blob/main/src/main/resources/templates/pages/randMarquee.html)與[randPic.html](https://github.com/vance0725/wedding-linebot/blob/main/src/main/resources/templates/pages/randPic.html)的程式內都會用以下JS，去除已中獎名單(和內定名單綁一起)
```
unassignUser(username);
```

# 相關URL
1. 編輯內定名單 \
/select-user
2. 篩選照片 \
/select-pic
3. 圖片&留言輪播 \
/page/imageShow.html
4. 文字抽獎 \
/page/randMarquee.html
5. 圖片抽獎 \
/page/randPic.html

# DEMO畫面
1. line傳送留言或照片 \
![image](https://raw.githubusercontent.com/vance0725/wedding-linebot/main/demo/line.JPG)
2. 照片會自動上傳至google相簿中 \
![image](https://raw.githubusercontent.com/vance0725/wedding-linebot/main/demo/image%20upload.PNG)
3. 螢幕上會輪播賓客上傳的照片及留言(黃色圓圈是留言者的line大頭照) \
![image](https://raw.githubusercontent.com/vance0725/wedding-linebot/main/demo/imageShow.png)
4. 文字抽獎結果(賓客圖片替換成喜，實際是該留言者的line大頭照) \
![image](https://raw.githubusercontent.com/vance0725/wedding-linebot/main/demo/randMarquee.png)
5. 圖片抽獎結果 \
![image](https://raw.githubusercontent.com/vance0725/wedding-linebot/main/demo/randPic.png)

