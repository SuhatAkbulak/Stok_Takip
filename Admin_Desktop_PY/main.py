from PyQt5 import QtWidgets, QtCore, uic
import sys
import requests

class Utils:
    @classmethod
    def messageBox(cls, title, message, type):
        error_box = QtWidgets.QMessageBox()
        error_box.setText(title)
        error_box.setInformativeText(message)
        error_box.setIcon(type)
        error_box.exec_()

class MainUi(QtWidgets.QDialog):
    def __init__(self, access_permission, token, parent=None):
        super(MainUi, self).__init__(parent)
        uic.loadUi('main.ui', self)

        self.token = token

        self.productsTree = self.findChild(QtWidgets.QTreeWidget, 'productsTree')
        self.productsButton = self.findChild(QtWidgets.QPushButton, 'getProductsButton')

        self.usernameTxt = self.findChild(QtWidgets.QTextEdit, 'usernameTxt')
        self.passwordTxt = self.findChild(QtWidgets.QTextEdit, 'passwordTxt')
        self.addUserButton = self.findChild(QtWidgets.QPushButton, 'addUserButton')
        self.accessPermissionComboBox = self.findChild(QtWidgets.QComboBox, 'accessPermissionComboBox')
        self.tabWidget = self.findChild(QtWidgets.QTabWidget, 'tabWidget')
        self.logTxt = self.findChild(QtWidgets.QTextEdit, 'logTxt')
        self.updateLogButton = self.findChild(QtWidgets.QPushButton, 'updateLogButton')

        self.productsButton.clicked.connect(self.productsButton_clicked)
        self.addUserButton.clicked.connect(self.addUserButton_clicked)
        self.updateLogButton.clicked.connect(self.updateLogButton_clicked)

        self.tabWidget.setTabEnabled(1, access_permission == 1)

        self.show()

    def productsButton_clicked(self):
        self.productsTree.clear()
        products = requests.get('https://proje.hegel.io/api/v1/list_stock').json()

        for product in products:
            productItem = QtWidgets.QTreeWidgetItem((product['urun_barkod'], product['urun_isim'], product['stok_sayi']))
            self.productsTree.addTopLevelItem(productItem)

    def addUserButton_clicked(self):
        username = self.usernameTxt.toPlainText()
        password = self.passwordTxt.toPlainText()

        if not username or not password:
            Utils.messageBox('Hata', 'Kullanıcı adı veya parola boş olamaz', QtWidgets.QMessageBox.Critical)
        else:
            resp = requests.post(
                'https://proje.hegel.io/api/v1/admin_creator',
                json={
                    'username': username,
                    'password': password,
                    'yetki': "2" if self.accessPermissionComboBox.currentIndex() == 0 else "1",
                    'token': self.token
                }
            ).json()

            if resp['message'] == 'Kullanıcı Başarılı Şekilde Oluşturuldu.':
                Utils.messageBox('Bilgi', 'Kullanıcı başarıla şekilde oluşturuldu', QtWidgets.QMessageBox.Information)
            else:
                Utils.messageBox('Hata', resp['message'], QtWidgets.QMessageBox.Information)

    def updateLogButton_clicked(self):
        resp = requests.get(
            'https://proje.hegel.io/api/v1/log_list',
            json={
                'token': self.token
            }
        ).json()

        content = ''

        for log in resp:
            content += log['tarih'] + '\n'
            content += log['events'] + '\n'
            content += 'İşlem Yapılan Ürün: ' + log['islem_yapilan_urun'] + '\n'
            content += 'İşlemi Yapan: ' + log['name'] + '\n'
            content += '\n'

        self.logTxt.setText(content)


class LoginUi(QtWidgets.QDialog):
    def __init__(self, parent=None):
        super(LoginUi, self).__init__(parent)
        uic.loadUi('login.ui', self)

        self.usernameTxt = self.findChild(QtWidgets.QTextEdit, 'usernameTxt')
        self.passwordTxt = self.findChild(QtWidgets.QTextEdit, 'passwordTxt')
        self.loginButton = self.findChild(QtWidgets.QPushButton, 'loginButton')

        self.loginButton.clicked.connect(self.loginButton_clicked)
        
        self.show()

    def loginButton_clicked(self):
        username = self.usernameTxt.toPlainText()
        password = self.passwordTxt.toPlainText()
        if not username or not password:
            Utils.messageBox('Hata', 'Kullanıcı adı veya parola boş olamaz', QtWidgets.QMessageBox.Critical)
        else:
            resp = requests.post(
                'https://proje.hegel.io/api/v1/login',
                json={
                    'username': username,
                    'password': password
                }
            ).json()

            if resp['message'] == 'Kullanıcı Adı Veya Şifreniz Hatalı':
                Utils.messageBox('Hata', 'Kullanıcı adı veya parola yanlış', QtWidgets.QMessageBox.Critical)
            elif resp['message'] == 'Oturumunuz başarılı şekilde açıldı':
                Utils.messageBox('Bilgi', 'Giriş yapıldı', QtWidgets.QMessageBox.Information)
                access_permission = resp['yetki']
                token = resp['token']
                self.hide()
                MainUi(access_permission, token, self).show()

            else:
                Utils.messageBox('Hata', resp['message'], QtWidgets.QMessageBox.Critical)


if __name__ == '__main__':
    app = QtWidgets.QApplication(sys.argv)
    app.setQuitOnLastWindowClosed(False)
    window = LoginUi()
    app.exec_()
