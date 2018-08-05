import {Component, NgZone, ViewChild} from '@angular/core';
import {AlertController, App, MenuController, Nav, Platform, ToastController} from 'ionic-angular';
import {StatusBar} from '@ionic-native/status-bar';
import {Push, PushObject, PushOptions} from '@ionic-native/push';
import {SplashScreen} from '@ionic-native/splash-screen';
import {Device} from '@ionic-native/device';
import {RestProvider} from "../providers/rest/rest";
import {TabsPage} from "../pages/tabs/tabs";
import {AppVersion} from "@ionic-native/app-version";
import {NotSupported} from "../pages/not-supported/not-supported";
import {SecureStorage, SecureStorageObject} from '@ionic-native/secure-storage';
import {Badge} from '@ionic-native/badge';
import {AppRate} from '@ionic-native/app-rate';
import {SocialSharing} from "@ionic-native/social-sharing";
import {ThemeProvider} from "../providers/theme/theme";
import {TranslateService} from '@ngx-translate/core';
import {IntroPage} from "../pages/intro/intro";


@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage: any = null;
  @ViewChild(Nav) navCtrl;
  selectedTheme: String;
  devidetoken: any;
  showRoot = false;
  pages: Array<{ id: any, title: string, component: any, iosicon: any, mdicon: any }>;

  public isCreated: boolean;
  public isApprove: boolean;
  public rateText: string;
  public inApp: boolean;
  public openNotification: boolean;

  constructor(public zone: NgZone,
              public push: Push,
              public platform: Platform,
              public app: App,
              private badge: Badge,
              public statusBar: StatusBar,
              private appRate: AppRate,
              private secureStorage: SecureStorage,
              private socialSharing: SocialSharing,
              public alertCtrl: AlertController,
              public authService: RestProvider,
              public toastCtrl: ToastController,
              public splashScreen: SplashScreen,
              public appVersion: AppVersion,
              public menu: MenuController,
              public device: Device,
              public translate: TranslateService,
              public themeProvider: ThemeProvider) {

    this.themeProvider.getActiveTheme().subscribe(val => this.selectedTheme = val);
    this.authService.setTheme(this.selectedTheme);

    this.initializeApp();
    //this.webOnly();
  }

  initializeApp() {
    this.platform.ready().then(() => {
      this.statusBar.backgroundColorByHexString('#243142');
      this.splashScreen.hide();
      this.isCreated = false;
      this.isApprove = false;
      this.authService.loadURL().then(() => {
        this.authService.getURL();
        this.appVersion.getVersionNumber().then((data) => {
            this.authService.loadUserCredentials();
            this.initPushNotification(data);
          }
        );
      });
      this.platform.resume.subscribe(() => {
        this.authService.loadUserCredentials();
        this.authService.loginOnly();
      });
    });
  }

  webOnly() {
    let code = 'en';
    this.authService.loadWebURL();
    if (window.localStorage.getItem('LANGUAGE')) {
      code = window.localStorage.getItem('LANGUAGE');
    }
    this.translate.setDefaultLang(code);
    this.authService.setLanguage(code)

    let currency = 'USD';
    if (window.localStorage.getItem('CURRENCY')) {
      currency = window.localStorage.getItem('CURRENCY');
    }
    this.authService.setCurrency(currency);

    let listStyle = 'card';
    if (window.localStorage.getItem('LIST_STYLE')) {
      listStyle = window.localStorage.getItem('LIST_STYLE');
    }
    this.authService.setListStyle(listStyle)

    this.authService.loadUserCredentials();
    this.showRoot = true;
    this.rootPage = TabsPage;
  }

  initPushNotification(version) {
    const options: PushOptions = {
      android: {senderID: "58802327413"},
      ios: {
        alert: 'true',
        badge: true,
        sound: 'true'
      },
      windows: {},
      browser: {
        pushServiceURL: 'http://push.api.phonegap.com/v1/push'
      }
    };
    const pushObject: PushObject = this.push.init(options);

    pushObject.on('notification').subscribe((data: any) => {
      //if user using app and push notification comes
      try {
        this.badge.increase(1);
      } catch (e) {

      }
      if (data.additionalData.foreground) {
        // if application open, show popup
        let confirmAlert = this.alertCtrl.create({
          title: data.title,
          message: data.message,
          buttons: [{
            text: 'Ignore',
            role: 'cancel'
          }, {
            text: 'View',
            handler: () => {
              this.openIcoView(data.additionalData);
            }
          }]
        });
        confirmAlert.present();
      } else {
        setTimeout(() => {
          this.openIcoView(data.additionalData);
        }, 1500);
      }
    });

    pushObject.on('registration').subscribe((data: any) => {
      this.isApprove = true;
      this.devidetoken = data.registrationId;
      this.authService.checkVersion(version).then((data: any) => {
        if (!(data))
          this.navCtrl.setRoot(NotSupported);
        else {
          this.initUser(version, this.devidetoken);
        }
      });
    });
    pushObject.on('read').subscribe((data: any) => {
      alert(JSON.stringify(data));
    });
    pushObject.on('delete').subscribe((data: any) => {
      alert(JSON.stringify(data));
    });

    pushObject.on('error').subscribe(error => {
      if (this.isApprove)
        return;
      this.presentToast('Please do vertical swipe to see ICO list.' + error);
      this.authService.checkVersion(version).then((data: any) => {
        if (!(data))
          this.navCtrl.setRoot(NotSupported);
        else {
          this.initUser(version, '');
        }
      });
    });
  }

  initUser(version, token) {
    if (this.platform.is('ios'))
      this.checkUser('IOS', this.device.uuid, token, version);
    else
      this.checkUser('ANDROID', this.device.uuid, token, version);
  }

  openIcoView(data) {
    if (this.openNotification)
      return;

    if (data.ids.indexOf(',') > 0 || data.alertType == 'NEWS') {
      this.openNotification = true;

      setTimeout(() => {
        this.openNotification = false;
      }, 10000);

      try {
        // this.app.getRootNav().push(AlertIcoPage, {
        //   list: data
        // });
      } catch (e) {

      }
    } else {
      // this.authService.getICOById(data.ids).subscribe((data: any) => {
      //   let current = new Date().getTime();
      //   let startTime = new Date(data.startTime).getTime();
      //   let endTime = new Date(data.endTime).getTime();
      //   let status = 0;
      //   if (current < startTime) {
      //     status = 2; //"UPCOMING"
      //   }
      //   else if (current > endTime) {
      //     status = 3; //past
      //   }
      //   else {
      //     status = 1; //"ONGOING"
      //   }
      //   try {
      //     // this.app.getRootNav().push(IcoViewPage, {
      //     //   item: data,
      //     //   status: status
      //     // });
      //   } catch (e) {
      //   }
      //
      // });
    }
  }


  uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }


  checkUser(type, deviceId, token, version) {
    if (type == 'IOS') {
      this.secureStorage.create('WALLET_ALARM')
        .then((storage: SecureStorageObject) => {
          let uuid = this.uuidv4();
          storage.get('WALLET_ALARM_UUID')
            .then(
              data => {
                if (data)
                  uuid = data;
                else
                  storage.set('WALLET_ALARM_UUID', uuid);
                this.checkUserFinal(uuid, token, version, type);
              },
              error => {
                storage.set('WALLET_ALARM_UUID', uuid);
                this.checkUserFinal(uuid, token, version, type);
              }
            );
        }).catch(err => {
        this.checkUserFinal(deviceId, token, version, type);
      });
    } else {
      this.checkUserFinal(deviceId, token, version, type);
    }
  }


  setVariables() {
    this.secureStorage.create('WALLET_ALARM')
      .then((storage: SecureStorageObject) => {
        let uuid = this.uuidv4();
        storage.get('WALLET_ALARM_LANG')
          .then(
            data => {
              let code = 'en';
              if (data) {
                code = data;
              }
              storage.set('WALLET_ALARM_LANG', code);
              this.translate.setDefaultLang(code);
              this.authService.setLanguage(code);
            },
            error => {
              storage.set('WALLET_ALARM_LANG', 'en');
              this.translate.setDefaultLang('en');
              this.authService.setLanguage('en');
            }
          );
        storage.get('WALLET_ALARM_CURRENCY')
          .then(
            data => {
              let code = 'USD';
              if (data) {
                code = data;
              }
              storage.set('WALLET_ALARM_CURRENCY', code);
              this.authService.setCurrency(code);
            },
            error => {
              storage.set('WALLET_ALARM_CURRENCY', 'USD');
              this.authService.setCurrency('USD');
            }
          );
        storage.get('WALLET_ALARM_LAYOUT')
          .then(
            data => {
              let code = 'card';
              if (data) {
                code = data;
              }
              storage.set('WALLET_ALARM_LAYOUT', code);
              this.authService.setListStyle(code);
            },
            error => {
              this.authService.setListStyle('card');
            }
          );
        storage.get('WALLET_ALARM_THEME')
          .then(
            data => {
              let code = 'dark-theme';
              if (data) {
                code = data;
              }
              storage.set('WALLET_ALARM_THEME', code);
              this.authService.setTheme(code);
            },
            error => {
              storage.set('WALLET_ALARM_THEME', 'dark-theme');
              this.authService.setTheme('dark-theme');
            }
          );
        storage.get('WALLET_ALARM_PASSCODE')
          .then(
            data => {
              let code = '';
              if (data) {
                code = data;
                this.authService.savePasscode(code);
              }
            }
          );
      }).catch(err => {
      this.authService.setTheme('dark-theme');
      this.authService.setListStyle('card');
      this.authService.setCurrency('USD');
      this.authService.setLanguage('en');

    });
  }

  checkUserFinal(deviceId, token, version, type) {
    if (type == 'IOS') {
      this.authService.getUserByToken(token)
        .subscribe((data: any) => {
          this.updateToken(data.userId, token, version);
        }, err => {
          this.authService.getUser(deviceId)
            .subscribe((data: any) => {
              this.updateToken(data.userId, token, version);
            }, err => {
              this.addUser(deviceId, token, version, type);
            });
        });
    } else {
      this.authService.getUser(deviceId)
        .subscribe((data: any) => {
          if(data && data.userId)
            this.updateToken(data.userId, token, version);
          else
            this.addUser(deviceId, token, version, type);
        }, err => {
          this.addUser(deviceId, token, version, type);
        });
    }

  }

  addUser(deviceId, token, version, type) {
    if (this.isCreated)
      return;
    this.isCreated = true;
    this.authService.addUser(deviceId, token, type, version).then(data => {
      if (data) {
        this._Navigate(true);
      } else {
        this._Navigate(true);
        this.presentToast('Failed to save details. Please restart application or contact support team.');
      }
    });
  }

  _Navigate(isnew) {
    this.setVariables();
    this.zone.run(() => {
      this.showRoot = true;
      if (isnew)
        this.navCtrl.setRoot('IntroPage');
      else {
        this.navCtrl.setRoot(TabsPage);
      }
      this.promptRate();
    });
  }


  updateToken(userId, token, version) {
    try {
      let inApp = window.localStorage.getItem('IN_APP');
      if (inApp == null || typeof(inApp) == 'undefined') {
        window.localStorage.setItem('IN_APP', "true");
        this.inApp = true;
      } else {
        this.inApp = JSON.parse(window.localStorage.getItem('IN_APP'));
      }
    } catch (e) {
    }

    if (token && token.length > 0) {
      this.authService.updateToken(userId, token, version).then(data => {
        if (data) {
          this._Navigate(false);
        } else {
          this._Navigate(false);
        }
      });
    }
    else {
      this._Navigate(false);
    }
  }

  promptRate() {
    this.appRate.preferences = {
      displayAppName: 'Wallet Alarm',
      usesUntilPrompt: 5,
      promptAgainForEachNewVersion: true,
      simpleMode: true,
      inAppReview: true,
      storeAppURL: {
        ios: '1308014716',
        android: 'market://details?id=com.icoalarm.app',
      },
      customLocale: {
        title: "Rate WalletAlarm",
        message: "It won’t take more than a minute and helps to promote our app.",
        cancelButtonLabel: "No, Thanks",
        laterButtonLabel: "Remind Me Later",
        rateButtonLabel: "Rate It Now",
        yesButtonLabel: "Yes!",
        noButtonLabel: "Already did"
      },
      callbacks: {
        handleNegativeFeedback: function () {
          window.open('mailto:walletalarm@gmail.com', '_system');
        },
        onButtonClicked: function (buttonIndex) {

        }
      }
    };
    setTimeout(() => {
      this.appRate.promptForRating(false);
    }, 4000);
  }

  presentToast(msg) {
    let toast = this.toastCtrl.create({
      message: msg,
      duration: 3000
    });
    toast.present();
  }
}
