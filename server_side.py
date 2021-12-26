from flask import Flask, request, jsonify, Response,session
from getpass import getpass
from datetime import datetime
import mysql.connector
import jwt
import time
import requests
import json

try:
  mydb = mysql.connector.connect(
    host="",
    user="",
    password="",
    database=""
  )
except:
    print('{"status": "no","message":"Veritabanı Bağlantısında Bir Sorun Oluştu."}')
    exit()


app = Flask(__name__)


@app.route("/api/v1/add_stock", methods=["GET", "POST"])
def add_stock():
    content = request.json

    if not content["barkod"]:
        return jsonify({"status": "no","message":"Ürün eklerken barkod zorunludur."})
        exit()
    else:
       barkod  = content["barkod"]

    if not content["urun_ismi"]:
       return jsonify({"status": "no","message":"Ürün İsmi girme zorunludur."})
       exit()
    else:
       urun_ismi  = content["urun_ismi"]

    if not content["stok_adet"]:
           return jsonify({"status": "no","message":"Stok Sayısı Girme Zorunludur."})
           exit()
    else:
       stok_adet  = content["stok_adet"]

    if not stok_adet.isnumeric():
           return jsonify({"status": "no","message":"Stok Sayısı Sadece Sayılardan Oluşması Gerekmektedir."})
           exit()


    if not content["token"]:
            return jsonify({"status": "no","message":"Yazılım güvenliği için stok eklemeden önce Giriş Yapmalısınız."})
            exit()
    else:
           token  = content["token"]

    try:

        MyData = jwt.decode(token, "MySecretIs190704905", algorithms=["HS256"])
        user_id = MyData["id"]
        exp = MyData["exp"]
        yetki = MyData["yetki"]

        if(exp < time.time()):
              return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
              exit()




    except:
        return jsonify({"status": "no","message":"Gönderdiğiniz Token Parametresi Geçersiz."})
        exit()


    mycursor = mydb.cursor()
    mycursor.execute("SELECT barkod_no FROM iUrunler WHERE barkod_no = %s",(barkod,))
    row = mycursor.fetchone()





    if row == None:
         mycursor = mydb.cursor()
         sql = "INSERT INTO iUrunler (barkod_no, urun_ismi,stok_adet) VALUES (%s, %s,%s)"
         val = (barkod, urun_ismi,stok_adet)
         mycursor.execute(sql, val)
         mydb.commit()

         ##SetEvents##
         mycursor = mydb.cursor()
         sql = "INSERT INTO iLogs (kullanici_id,barkod,events) VALUES (%s,%s,%s)"
         events = ""+stok_adet+" Adet Ürün Ekledi!"
         val = (user_id, barkod,events)
         mycursor.execute(sql, val)
         mydb.commit()
         ##SetEvents##
         return  jsonify({"status": "ok","message":"Ürün Eklendi","totalStock":mycursor.rowcount})
    else:
         mycursor = mydb.cursor()
         sql = "UPDATE iUrunler SET stok_adet = %s WHERE barkod_no = %s"
         val = (stok_adet, barkod)
         mycursor.execute(sql, val)
         mydb.commit()

         ##SetEvents##
         mycursor = mydb.cursor()
         sql = "INSERT INTO iLogs (kullanici_id,barkod,events) VALUES (%s,%s,%s)"
         events = "Ürün Olduğundan Dolayı,Stoğun Adedi "+stok_adet+" Olarak Güncellendi!"
         val = (user_id, barkod,events)
         mycursor.execute(sql, val)
         mydb.commit()
         ##SetEvents##

         return  jsonify({"status": "ok","message":"Ürün olduğu için sadece stok güncellendi !"})
         exit()

    return  jsonify({"status": "ok"})


@app.route("/api/v1/list_stock", methods=["GET", "POST"])
def list_urun():
    content = request.json
    mycursor = mydb.cursor()
    mycursor.execute("SELECT * FROM iUrunler")
    myresult = mycursor.fetchall()


    dizi = []


    for x in myresult:
      urun_id = x[0]
      urun_barkod = x[1]
      urun_isim = x[2]
      stok_sayi = x[3]
      i = {"urun_id": urun_id, "urun_barkod": urun_barkod,"urun_isim":urun_isim,"stok_sayi":stok_sayi}
      dizi.append(i)


    return jsonify(dizi)


@app.route("/api/v1/login", methods=["GET", "POST"])
def login():
    content = request.json

    if not content["username"]:
            return jsonify({"status": "no","message":"Oturum açmak için kullanıcı adı girmeniz zorunludur."})
            exit()
    else:
             username  = content["username"]

    if not content["password"]:
             return jsonify({"status": "no","message":"Oturum açmak için şifre girmeniz zorunludur."})
             exit()
    else:
            password  = content["password"]


    mycursor = mydb.cursor()
    mycursor.execute('SELECT * FROM iKullanici WHERE username = %s AND password = %s', (username, password,))
    account = mycursor.fetchone()
    if account:
            login_time = time.time()
            expreid = login_time + 86400
            Jwt_Auth = jwt.encode({"time":login_time,"exp":expreid, "id": account[0],"yetki":account[3]}, "MySecretIs190704905", algorithm="HS256")
            return jsonify({"status": "ok","exp":expreid,"login_time":login_time,"username":account[1],"yetki":account[3],"token":Jwt_Auth,"message":"Oturumunuz başarılı şekilde açıldı"})
            exit()
    else:
            return jsonify({"status": "no","message":"Kullanıcı Adı Veya Şifreniz Hatalı"})
            exit()



    return jsonify()


@app.route("/api/v1/edit_urun/<urunid>", methods=["GET", "POST"])
def edit_urun(urunid):
    content = request.json

    if not content["token"]:
                return jsonify({"status": "no","message":"Stokdaki Ürünü Değiştirmeden Önce Giriş Yapmanız Gerekmektedir."})
                exit()
    else:
               token  = content["token"]

    if not urunid:
           return jsonify({"status": "no","message":"Değişiklik Yapmak İstiğiniz Ürün Idsini Giriniz."})
           exit()
    else:
           urun_id  = urunid

    if not content["action"]:
            return jsonify({"status": "no","message":"Eylem Girilmesi Zorunludur"})
            exit()
    else:
           action  = content["action"]


    if not content["new_query"]:
               return jsonify({"status": "no","message":"Güncellenecek Stok Ismi Veya Ürün İsmi Boş Bırakılamaz."})
               exit()
    else:
                  new_query  = content["new_query"]

    ## Token Control ##
    try:

            MyData = jwt.decode(token, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()

            if(yetki != 2):
                  return jsonify({"status": "no","message":"Sadece Çalışan Statüsündeki Kişiler Stokdaki Ürünleri Düzenleyenilir."})
                  exit()

    except:
            return jsonify({"status": "no","message":"Gönderdiğiniz Token Parametresi Geçersiz."})
            exit()
    ## Token Control ##



    if(action == "urun_ismi_degis"):
             mycursor = mydb.cursor()
             mycursor.execute('SELECT * FROM iUrunler WHERE id = %s', ( urun_id,))
             urun_eski_detay = mycursor.fetchone()
             barkod_no = urun_eski_detay[1]
             eski_isim = urun_eski_detay[2]
             eski_stok = urun_eski_detay[3]
             mycursor = mydb.cursor()
             sql = "UPDATE iUrunler SET urun_ismi = %s WHERE id = %s"
             val = (new_query, urun_id)
             mycursor.execute(sql, val)
             mydb.commit()
             ##SetEvents##
             mycursor = mydb.cursor()
             sql = "INSERT INTO iLogs (kullanici_id,barkod,events) VALUES (%s,%s,%s)"
             events = ""+eski_isim+" İsimli Ürünün İsmini Değiştirdi!"
             val = (user_id, barkod_no,events)
             mycursor.execute(sql, val)
             mydb.commit()
             ##SetEvents##
             return jsonify({"status": "ok","message":"Stokdaki Ürünün İsmini Başarılı Şekilde Değiştirildi."})
             exit()

    if(action == "urun_stok_degis"):
             if not new_query.isnumeric():
                   return jsonify({"status": "no","message":"Yeni Stok Miktarı Sadece Sayıdan Oluşmalıdır."})
                   exit()

             mycursor = mydb.cursor()
             mycursor.execute('SELECT * FROM iUrunler WHERE id = %s', ( urun_id,))
             urun_eski_detay = mycursor.fetchone()
             barkod_no = urun_eski_detay[1]
             eski_isim = urun_eski_detay[2]
             eski_stok = urun_eski_detay[3]
             mycursor = mydb.cursor()
             sql = "UPDATE iUrunler SET stok_adet = %s WHERE id = %s"
             val = (new_query, urun_id)
             mycursor.execute(sql, val)
             mydb.commit()
             ##SetEvents##
             mycursor = mydb.cursor()
             sql = "INSERT INTO iLogs (kullanici_id,barkod,events) VALUES (%s,%s,%s)"
             events = ""+eski_stok+" Stok Sayısına Ait Olan Ürünün Stok Sayısı Değişti."
             val = (user_id, barkod_no,events)
             mycursor.execute(sql, val)
             mydb.commit()
             ##SetEvents##
             return jsonify({"status": "ok","message":"Stokdaki Ürünün Sayısını Başarılı Şekilde Değiştirildi."})
             exit()



    return jsonify({})



@app.route("/api/v1/delete_urun/<urunid>", methods=["GET", "POST"])
def delete_urun(urunid):
    content = request.json

    if not urunid:
              return jsonify({"status": "no","message":"Değişiklik Yapmak İstiğiniz Ürün Idsini Giriniz."})
              exit()
    else:
              urun_id  = urunid

    if not content["token"]:
               return jsonify({"status": "no","message":"Yazılım güvenliği için ürün silmeden önce Giriş Yapmalısınız."})
               exit()
    else:
               tokens  = content["token"]

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()

            if(yetki != 2):
                  return jsonify({"status": "no","message":"Sadece Çalışan Statüsündeki Kişiler Stokdaki Ürünleri Düzenleyenilir."})
                  exit()

            mycursor = mydb.cursor()
            mycursor.execute('SELECT * FROM iUrunler WHERE id = %s', ( urun_id,))
            urun_eski_detay = mycursor.fetchone()
            barkod_no = urun_eski_detay[1]
            eski_isim = urun_eski_detay[2]
            eski_stok = urun_eski_detay[3]

            mycursor = mydb.cursor()
            sql = "DELETE FROM iUrunler WHERE id = %s"
            val = (urun_id,)
            mycursor.execute(sql, val)
            mydb.commit()

            ##SetEvents##
            mycursor = mydb.cursor()
            sql = "INSERT INTO iLogs (kullanici_id,barkod,events) VALUES (%s,%s,%s)"
            events = ""+eski_isim+" İsimli Ürünü Stokdan Sildi!"
            val = (user_id, barkod_no,events)
            mycursor.execute(sql, val)
            mydb.commit()
            ##SetEvents##
            return jsonify({"status": "ok","message":"Stokdaki Ürünün Başarılı Şekilde Silindi."})
            exit()

    except:
            return jsonify({"status": "no","message":"Böyle Bir Ürün Zaten Yok."})
            exit()
    ## Token Control ##







@app.route("/api/v1/stok_tekli_artis", methods=["GET", "POST"])
def stok_tekli_artis():
    content = request.json

    if not content["barkod"]:
               return jsonify({"status": "no","message":"Stoğa Ürün Eklemek İçin Lütfen Barkodunu Okutunuz."})
               exit()
    else:
               barkod  = content["barkod"]


    if not content["token"]:
                return jsonify({"status": "no","message":"Stokdaki Ürünleri Artırtmak İçin Giriş Yapmanız Gerekmektedir."})
                exit()
    else:
                tokens  = content["token"]

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()



            mycursor = mydb.cursor()
            mycursor.execute('SELECT * FROM iUrunler WHERE barkod_no = %s', ( barkod,))
            urun_eski_detay = mycursor.fetchone()

            if not urun_eski_detay[3]:
                     return jsonify({"status": "no","message":"Bu Barkoda Tanımlı Ürün Bulunamadı.Lütfen Yöneticinizden Eklemesini İsteyin Yada 'Barkod İle Stok Ekle' Bölümünden İlk Önce Ürünü Tanımlayın. "})
                     exit()
            else:
                     eski_stok  = urun_eski_detay[3]
                     i = int(eski_stok)

            yeni_stok_sayi = i + 1
            mycursor = mydb.cursor()
            sql = "UPDATE iUrunler SET stok_adet = %s WHERE barkod_no = %s"
            val = (yeni_stok_sayi, barkod)
            mycursor.execute(sql, val)
            mydb.commit()

            ##SetEvents##
            mycursor = mydb.cursor()
            sql = "INSERT INTO iLogs (kullanici_id,barkod,events) VALUES (%s,%s,%s)"
            events = "Stoğa Yeni Ürün Ekledi."
            val = (user_id, barkod,events)
            mycursor.execute(sql, val)
            mydb.commit()
            ##SetEvents##
            return jsonify({"status": "ok","message":"Ürün Stoğuna Bir Adet Ürün Eklediniz."})
            exit()

    except:
            return jsonify({"status": "no","message":"Oturumunuz Sonlanmış Olabilir Lütfen Giriş Yapın."})
            exit()
    ## Token Control ##



@app.route("/api/v1/stok_tekli_azalis", methods=["GET", "POST"])
def stok_tekli_azalis():
    content = request.json

    if not content["barkod"]:
               return jsonify({"status": "no","message":"Stokdan Ürün Çıkartmak İçin Lütfen Barkodunu Okutunuz."})
               exit()
    else:
               barkod  = content["barkod"]


    if not content["token"]:
                return jsonify({"status": "no","message":"Stokdaki Ürünlerin Miktarını Azaltmak İçin Giriş Yapmanız Gerekmektedir."})
                exit()
    else:
                tokens  = content["token"]

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()



            mycursor = mydb.cursor()
            mycursor.execute('SELECT * FROM iUrunler WHERE barkod_no = %s', ( barkod,))
            urun_eski_detay = mycursor.fetchone()

            if not urun_eski_detay[3]:
                     return jsonify({"status": "no","message":"Bu Barkoda Tanımlı Ürün Bulunamadı.Lütfen Yöneticinizden Eklemesini İsteyin Yada 'Barkod İle Stok Ekle' Bölümünden İlk Önce Ürünü Tanımlayın. "})
                     exit()
            else:
                     eski_stok  = urun_eski_detay[3]
                     i = int(eski_stok)
            if (i < 0):
                    return jsonify({"status": "no","message":"Bu Ürün İçin Stok Kalmadı."})
                    exit()


            yeni_stok_sayi = i - 1
            mycursor = mydb.cursor()
            sql = "UPDATE iUrunler SET stok_adet = %s WHERE barkod_no = %s"
            val = (yeni_stok_sayi, barkod)
            mycursor.execute(sql, val)
            mydb.commit()

            ##SetEvents##
            mycursor = mydb.cursor()
            sql = "INSERT INTO iLogs (kullanici_id,barkod,events) VALUES (%s,%s,%s)"
            events = "Stokdan Bir Adet Ürün Çıkışı Yaptı."
            val = (user_id, barkod,events)
            mycursor.execute(sql, val)
            mydb.commit()
            ##SetEvents##
            return jsonify({"status": "ok","message":"Ürün Stoğundan Bir Adet Ürün Çıkışı Yaptınız."})
            exit()

    except:
            return jsonify({"status": "no","message":"Böyle Bir Ürün Zaten Yok."})
            exit()
    ## Token Control ##


@app.route("/api/v1/log_list", methods=["GET", "POST"])
def log_list():
    content = request.json

    if not content["token"]:
                return jsonify({"status": "no","message":"Çalışan Aktivitelerini Görüntüleyebilmek İçin Giriş Yapmanız Gerekmektedir."})
                exit()
    else:
                tokens  = content["token"]

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()

            if(yetki != 1):
                  return jsonify({"status": "no","message":"Çalışan Etkinliklerini Sadece Yönetici Görüntülüyebilir."})
                  exit()



            content = request.json
            mycursor = mydb.cursor()
            mycursor.execute("SELECT * FROM iLogs")
            myresult = mycursor.fetchall()

            dizi = []




            for x in myresult:
               id = x[0]
               kullanici_id = x[1]





               userinfo = mydb.cursor()
               userinfo.execute("SELECT username FROM iKullanici WHERE id = %s",(kullanici_id,))
               row = userinfo.fetchone()
               islemiyapanname = row[0]



               islem_yapilan_urun = x[2]
               events = x[3]
               i = {"db_id": id, "kullanici_id": kullanici_id,"islem_yapilan_urun":islem_yapilan_urun,"events":events,"name":islemiyapanname}
               dizi.append(i)

            return jsonify(dizi)
            exit()

    except:
            return jsonify({"status": "no","message":"Bir Sorun Oluştu !"})
            exit()
    ## Token Control ##






    return jsonify({})


@app.route("/api/v1/admin_creator", methods=["GET", "POST"])
def admin_creator():
    content = request.json

    if not content["token"]:
                    return jsonify({"status": "no","message":"Çalışan Aktivitelerini Görüntüleyebilmek İçin Giriş Yapmanız Gerekmektedir."})
                    exit()
    else:
                    tokens  = content["token"]


    if not content["username"]:
                    return jsonify({"status": "no","message":"Yeni kullanıcı oluşturmak için kullanıcı adı kısmını boş bırakamazsınız."})
                    exit()
    else:
                    username  = content["username"]

    if not content["password"]:
                    return jsonify({"status": "no","message":"Yeni kullanıcı oluşturmak için şifre alanını boş bırakamazsınız."})
                    exit()
    else:
                    password  = content["password"]


    if not content["yetki"]:
                     return jsonify({"status": "no","message":"Sisteme ekleyeceğiniz kullanıcıya vereceğiniz yetkiyi seçin."})
                     exit()
    else:
                     yetkis  = content["yetki"]

    if not yetki.isnumeric():
                     return jsonify({"status": "no","message":"Yetki Sadece sayılardan oluşturulmalıdır."})
                     exit()

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()

            if(yetki != 1):
                  return jsonify({"status": "no","message":"Yönetici Eklemek İçin Yönetici Konumında Olmalısınız."})
                  exit()



            mycursor = mydb.cursor()

            sql = "INSERT iKullanici (username, password,yetki) VALUES (%s, %s,%s)"
            val = (username, password,yetkis)
            mycursor.execute(sql, val)

            mydb.commit()

            return jsonify({"status": "ok","message":"Kullanıcı Başarılı Şekilde Oluşturuldu."})
            exit()

    except:
            return jsonify({"status": "no","message":"Bir Sorun Oluştu !"})
            exit()
    ## Token Control ##






    return jsonify({})



@app.route("/api/v1/list_yonetici", methods=["GET", "POST"])
def list_yonetici():
    content = request.json

    if not content["token"]:
                return jsonify({"status": "no","message":"Sistem Kullanıcılarını Görmek İçin Giriş Yapmanız Gerekmekte."})
                exit()
    else:
                tokens  = content["token"]

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()

            if(yetki != 1):
                  return jsonify({"status": "no","message":"Üyeleri Sadece Yönetici Görüntülüyebilir."})
                  exit()



            content = request.json
            mycursor = mydb.cursor()
            mycursor.execute("SELECT * FROM iKullanici")
            myresult = mycursor.fetchall()

            dizi = []




            for x in myresult:
               id = x[0]
               username = x[1]
               password = x[2]
               yetki = x[3]


               i = {"db_id": id, "username": username,"password":'*****'}
               dizi.append(i)

            return jsonify(dizi)
            exit()

    except:
            return jsonify({"status": "no","message":"Bir Sorun Oluştu !"})
            exit()
    ## Token Control ##

            return jsonify({})



@app.route("/api/v1/delete_user", methods=["GET", "POST"])
def delete_user():
    content = request.json

    if not content["token"]:
                return jsonify({"status": "no","message":"Sistemde Kullanıcı Silmek İçin İlk Önce Giriş Yapmalısınız."})
                exit()
    else:
                tokens  = content["token"]

    if not content["user_id"]:
                return jsonify({"status": "no","message":"Silecek Kullanıcının Idsini Yazın."})
                exit()
    else:
                user_id  = content["user_id"]

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()

            if(yetki != 1):
                  return jsonify({"status": "no","message":"Üyeleri Sadece Yönetici Görüntülüyebilir."})
                  exit()



            mycursor = mydb.cursor()
            sql = "DELETE FROM iKullanici WHERE id = %s"
            val = (user_id,)
            mycursor.execute(sql, val)
            mydb.commit()

            return jsonify({"status": "ok","message":"Üye Başarılı Şekilde Silindi."})
            exit()

    except:
            return jsonify({"status": "no","message":"Bir Sorun Oluştu !"})
            exit()
    ## Token Control ##

            return jsonify({})


@app.route("/api/v1/dashbord_info", methods=["GET", "POST"])
def dashbord_info():
    content = request.json

    if not content["token"]:
                return jsonify({"status": "no","message":"Sistem Bilgilerini Görüntülüyebilmek İçin İlk Önce Giriş Yapın."})
                exit()
    else:
                tokens  = content["token"]

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()

            if(yetki != 1):
                  return jsonify({"status": "no","message":"Sistem Bilgilerini Sadece Yönetici Görüntülüyebilir."})
                  exit()





            userinfo = mydb.cursor()
            userinfo.execute("SELECT COUNT(barkod_no) FROM iUrunler")
            row = userinfo.fetchone()

            userinfo.execute("SELECT SUM(stok_adet) FROM iUrunler")
            row2 = userinfo.fetchone()

            userinfo.execute("SELECT COUNT(username) FROM iKullanici")
            row3 = userinfo.fetchone()

            toplam_urun_sayisi = row[0]
            depodaki_toplam_sayi = row2[0]
            sistemdeki_toplam_kullanici = row3[0]


            cookies = {
                '_ga': 'GA1.2.1658004177.1640077080',
                '_gid': 'GA1.2.427277279.1640505722',
            }

            headers = {
                'Connection': 'keep-alive',
                'sec-ch-ua': '" Not A;Brand";v="99", "Chromium";v="96", "Google Chrome";v="96"',
                'Accept': '*/*',
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest',
                'sec-ch-ua-mobile': '?0',
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4364.115 Safari/537.36',
                'sec-ch-ua-platform': '"macOS"',
                'Origin': 'https://www.haremaltin.com',
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Referer': 'https://www.haremaltin.com/canli-piyasalar/doviz-kurlari',
                'Accept-Language': 'tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7',
            }

            data = {
              'dil_kodu': 'tr'
            }

            response = requests.post('https://www.haremaltin.com/dashboard/ajax/doviz', headers=headers, cookies=cookies, data=data)
            data = response.json()
            USDTRY = data["data"]["USDTRY"]
            EURUSD = data["data"]["EURUSD"]
            ALTIN = data["data"]["ALTIN"]
            CEYREK_ESKI = data["data"]["CEYREK_ESKI"]
            YARIM_ESKI = data["data"]["YARIM_ESKI"]





            return jsonify({"status": "ok","toplam_urun":toplam_urun_sayisi,"depodaki_toplam_urun":depodaki_toplam_sayi,"sistemdeki_toplam_kullanici":sistemdeki_toplam_kullanici,"USDTRY":USDTRY,"EURUSD":EURUSD,"ALTIN":ALTIN,"CEYREK_ESKI":CEYREK_ESKI,"YARIM_ESKI":YARIM_ESKI})
            exit()

    except:
            return jsonify({"status": "no","message":"Bir Sorun Oluştu !"})
            exit()
    ## Token Control ##

            return jsonify({})


@app.route("/api/v1/guncel_dolar_kur", methods=["GET", "POST"])
def guncel_dolar_kur():
    content = request.json

    if not content["token"]:
                return jsonify({"status": "no","message":"Döviz Kurlarını Görüntülüyebilmek İçin İlk Önce Giriş Yapın."})
                exit()
    else:
                tokens  = content["token"]

    ## Token Control ##
    try:


            MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
            user_id = MyData["id"]
            exp = MyData["exp"]
            yetki = MyData["yetki"]

            if(exp < time.time()):
                  return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                  exit()







            cookies = {
                '_ga': 'GA1.2.1658004177.1640077080',
                '_gid': 'GA1.2.427277279.1640505722',
            }

            headers = {
                'Connection': 'keep-alive',
                'sec-ch-ua': '" Not A;Brand";v="99", "Chromium";v="96", "Google Chrome";v="96"',
                'Accept': '*/*',
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest',
                'sec-ch-ua-mobile': '?0',
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4364.115 Safari/537.36',
                'sec-ch-ua-platform': '"macOS"',
                'Origin': 'https://www.haremaltin.com',
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Referer': 'https://www.haremaltin.com/canli-piyasalar/doviz-kurlari',
                'Accept-Language': 'tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7',
            }

            data = {
              'dil_kodu': 'tr'
            }

            response = requests.post('https://www.haremaltin.com/dashboard/ajax/doviz', headers=headers, cookies=cookies, data=data)
            data = response.json()
            return data





            return jsonify({"status": "ok","message":"Üye Başarılı Şekilde Silindi."})
            exit()

    except:
            return jsonify({"status": "no","message":"Bir Sorun Oluştu !"})
            exit()
    ## Token Control ##

            return jsonify({})


@app.route("/api/v1/delete_all_log_list", methods=["GET", "POST"])
def delete_all_log_list():
      content = request.json

      if not content["token"]:
                  return jsonify({"status": "no","message":"Tüm Logları Silmek İçin Giriş Yapmanız Gerekmekte"})
                  exit()
      else:
                  tokens  = content["token"]

      ## Token Control ##
      try:


              MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
              user_id = MyData["id"]
              exp = MyData["exp"]
              yetki = MyData["yetki"]

              if(exp < time.time()):
                    return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                    exit()

              if(yetki != 1):
                    return jsonify({"status": "no","message":"Çalışan Etkinliklerini Silmek İçin Yönetici Olmalısınız."})
                    exit()



              mycursor = mydb.cursor()
              mycursor.execute("truncate iLogs")
              row = mycursor.fetchone()

              return jsonify({"status": "ok","message":"Kullanıcı Etkinlikleri Başarılı Şekilde Silindi."})
              exit()

      except:
              return jsonify({"status": "no","message":"Bir Sorun Oluştu."})
              exit()
      ## Token Control ##






      return jsonify({})


@app.route("/api/v1/delete_all_tum_urunler", methods=["GET", "POST"])
def delete_all_tum_urunler():
      content = request.json

      if not content["token"]:
                  return jsonify({"status": "no","message":"Tüm Ürünleri Silmek İçin Giriş Yapmanız Gerekmekte"})
                  exit()
      else:
                  tokens  = content["token"]

      ## Token Control ##
      try:


              MyData = jwt.decode(tokens, "MySecretIs190704905", algorithms=["HS256"])
              user_id = MyData["id"]
              exp = MyData["exp"]
              yetki = MyData["yetki"]

              if(exp < time.time()):
                    return jsonify({"status": "no","message":"Oturumunuz Sonlanmış.Lütfen Tekrar Giriş Yapın."})
                    exit()

              if(yetki != 1):
                    return jsonify({"status": "no","message":"Stokda Bulunan Tüm Ürünleri Silmek İçin Yönetici Olmalısınız."})
                    exit()



              mycursor = mydb.cursor()
              mycursor.execute("truncate iUrunler")
              row = mycursor.fetchone()

              return jsonify({"status": "ok","message":"Stokdaki Ürünler Başarılı Şekilde Silindi."})
              exit()

      except:
              return jsonify({"status": "no","message":"Bir Sorun Oluştu."})
              exit()
      ## Token Control ##






      return jsonify({})



if __name__ == "__main__":
    app.run(host="192.168.1.15", port=5001, debug=True)
