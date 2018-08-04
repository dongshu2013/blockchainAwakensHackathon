import {Component, NgZone} from '@angular/core';
import {AlertController, Events, IonicPage, NavController, NavParams} from 'ionic-angular';
import {FeedbackPage} from "../feedback/feedback";
import {TranslateService} from "@ngx-translate/core";
import {ThemeProvider} from "../../providers/theme/theme";
import {RestProvider} from "../../providers/rest/rest";
import {GenerateQrCodePage} from "../generate-qr-code/generate-qr-code";
import {PortDevicePage} from "../port-device/port-device";
import {PinScreenPage} from "../pin-screen/pin-screen";

/**
 * Generated class for the SettingsPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-settings',
  templateUrl: 'settings.html',
})
export class SettingsPage {

  themeSegment = 'dark-theme';
  listSegment = 'card';
  toggleColor = 'toggle-color';
  public currentPasscode = null;
  public title = 'Settings'
  public isPasscode = false;
  public review: string;
  public reviewIcon: string;
  public inApp: boolean = true;
  language: string;
  public currencyCode = 'USD';
  public currencyName = 'US Dollar (USD)';
  subHeader: boolean = false;

  public currency = [
    {
      value: 'USD',
      type: 'radio',
      label: 'US Dollar (USD)'
    },
    // {
    //   value: 'BTC',
    //   type: 'radio',
    //   label: 'Bitcoin (BTC)'
    // },
    // {
    //   value: 'BCH',
    //   type: 'radio',
    //   label: 'Bitcoin Cash (BCH)'
    // },
    // {
    //   value: 'ETH',
    //   type: 'radio',
    //   label: 'Etherium (ETH)'
    // },
    // {
    //   value: 'XRP',
    //   type: 'radio',
    //   label: 'Ripple (XRP)'
    // },
    // {
    //   value: 'LTC',
    //   type: 'radio',
    //   label: 'Litecoin (LTC)'
    // },
    {
      value: 'AUD',
      type: 'radio',
      label: 'Australian Dollar (AUD)'
    },
    {
      value: 'BRL',
      type: 'radio',
      label: 'Brazilian Real (BRL)'
    },
    {
      value: 'CAD',
      type: 'radio',
      label: 'Canadian Dollar (CAD)'
    },
    {
      value: 'CHF',
      type: 'radio',
      label: 'Swiss Franc (CHF)'
    },
    {
      value: 'CLP',
      type: 'radio',
      label: 'Chilean Peso (CLP)'
    },
    {
      value: 'CNY',
      type: 'radio',
      label: 'Chinese Yuan (CNY)'
    },
    {
      value: 'CZK',
      type: 'radio',
      label: 'Czech Koruna (CZK)'
    },
    {
      value: 'DKK',
      type: 'radio',
      label: 'Danish Krone (DKK)'
    },
    {
      value: 'EUR',
      type: 'radio',
      label: 'Euro (EUR)'
    },
    {
      value: 'GBP',
      type: 'radio',
      label: 'Pound (GBP)'
    },
    {
      value: 'HKD',
      type: 'radio',
      label: 'Hong Kong (HKD)'
    },
    {
      value: 'HUF',
      type: 'radio',
      label: 'Hungarian Forint (HUF)'
    },
    {
      value: 'IDR',
      type: 'radio',
      label: 'Indonesian Rupiah (IDR)'
    },
    {
      value: 'ILS',
      type: 'radio',
      label: 'Israeli New Shekel (ILS)'
    },
    {
      value: 'INR',
      type: 'radio',
      label: 'Indian Rupee (INR)'
    },
    {
      value: 'JPY',
      type: 'radio',
      label: 'Japanese Yen (JPY)'
    },
    {
      value: 'KRW',
      type: 'radio',
      label: 'South Korean won (KRW)'
    },
    {
      value: 'MXN',
      type: 'radio',
      label: 'Mexican Peso (MXN)'
    },
    {
      value: 'MYR',
      type: 'radio',
      label: 'Malaysian Ringgit (MYR)'
    },

    {
      value: 'NOK',
      type: 'radio',
      label: 'Norwegian Krone (NOK)'
    },
    {
      value: 'NZD',
      type: 'radio',
      label: 'New Zealand Dollar (NZD)'
    },
    {
      value: 'PHP',
      type: 'radio',
      label: 'Philippine Piso (PHP)'
    },
    {
      value: 'PKR',
      type: 'radio',
      label: 'Pakistani Rupee (PKR)'
    },
    {
      value: 'PLN',
      type: 'radio',
      label: 'Poland złoty (PLN)'
    },
    {
      value: 'RUB',
      type: 'radio',
      label: 'Russian Ruble (RUB)'
    },
    {
      value: 'SEK',
      type: 'radio',
      label: 'Swedish Krona (SEK)'
    },
    {
      value: 'SGD',
      type: 'radio',
      label: 'Singapore Dollar (SGD)'
    },
    {
      value: 'THB',
      type: 'radio',
      label: 'Thai Baht (THB)'
    },


    {
      value: 'TRY',
      type: 'radio',
      label: 'Español'
    },
    {
      value: 'TWD',
      type: 'radio',
      label: 'Español'
    },
    {
      value: 'ZAR',
      type: 'radio',
      label: 'Español'
    }

  ]
  public lans = [
    {
      value: 'en',
      type: 'radio',
      label: 'English'
    },
    {
      value: 'ru',
      type: 'radio',
      label: 'Pусский'
    },
    {
      value: 'cn',
      type: 'radio',
      label: '普通话'
    },
    {
      value: 'sn',
      type: 'radio',
      label: 'Dutch'
    },
    {
      value: 'pt',
      type: 'radio',
      label: 'Portuguese'
    },
    {
      value: 'es',
      type: 'radio',
      label: 'Español'
    }
  ]


  constructor(public navCtrl: NavController, public navParams: NavParams,
              public translate: TranslateService,
              public events: Events,
              public authService: RestProvider,
              private _ngZone: NgZone,
              private themeProvider: ThemeProvider,
              private alertCtrl: AlertController) {

    this.initPage();
    this.events.subscribe('header:change', (state) => {
      this._ngZone.run(() => {
        this.subHeader = state;
      });
    });

    this.events.subscribe('passcode:change', (state) => {
      this.setPasscode();
    });
  }

  initPage() {
    let code = window.localStorage.getItem('LANGUAGE');
    this.setLanguage(code);
    this.setPasscode();
    this.currencyCode = this.authService.getCurrency();
    this.setCurrency(this.currencyCode);

    this.review = 'Play store Rate & Review';
    this.reviewIcon = 'logo-android';
    this.inApp = JSON.parse(window.localStorage.getItem('IN_APP'));
    this.listSegment = this.authService.getListStyle();
    this.themeSegment = this.authService.getTheme();

    this.translate.get('settings_title').subscribe((res: string) => {
      this.title = res;
    });
  }

  swipe(e) {
    if (e.direction === 4) {
      this.navCtrl.parent.select(1);
    }
  }

  setPasscode() {
    this.currentPasscode = this.authService.getPasscode();
    if (this.currentPasscode != null) {
      this.isPasscode = true;
    }
  }

  changeTheme() {
    this.themeProvider.setActiveTheme(this.themeSegment);
    this.toggleColor = this.themeSegment + '-toggle';
    this.authService.setTheme(this.themeSegment);
    this.events.publish('chart:load');
  }

  changeListStyle() {
    this.authService.setListStyle(this.listSegment);
    this.events.publish('layout:load');
  }

  setLanguage(code) {
    for (let i = 0; i < this.lans.length; i++) {
      if (this.lans[i].value == code) {
        this.lans[i]['checked'] = true;
        this.language = this.lans[i].label;
        this.translate.setDefaultLang(this.lans[i].value);
      } else {
        this.lans[i]['checked'] = false;
      }
    }
  }

  setCurrency(code) {
    for (let i = 0; i < this.currency.length; i++) {
      if (this.currency[i].value == code) {
        this.currency[i]['checked'] = true;
        this.currencyName = this.currency[i].label;
      } else {
        this.currency[i]['checked'] = false;
      }
    }
  }

  async presentChangeLanguage() {
    const alert = await this.alertCtrl.create({
      title: 'Chose Language',
      inputs: this.lans,
      cssClass: this.themeSegment,
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => {
            console.log('Confirm Cancel')
          }
        }, {
          text: 'Select',
          handler: data => {
            for (let i = 0; i < this.lans.length; i++) {
              if (this.lans[i].value == data) {
                this.language = this.lans[i].label;
                break;
              }
            }
            this.authService.setLanguage(data);
            this.setLanguage(data);
          }
        }
      ]
    });

    await alert.present();
  }

  async presentChangeCurrency() {
    const alert = await this.alertCtrl.create({
      title: 'Change Currency',
      inputs: this.currency,
      cssClass: this.themeSegment,
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary'
        }, {
          text: 'Select',
          handler: data => {
            for (let i = 0; i < this.lans.length; i++) {
              if (this.lans[i].value == data) {
                this.language = this.lans[i].label;
                break;
              }
            }
            this.authService.setCurrency(data);
            this.setCurrency(data);
            this.currencyCode = data;
            this.events.publish('currency:change', data);
          }
        }
      ]
    });

    await alert.present();
  }

  passcode() {
    this.navCtrl.push('PinScreenPage');
  }

  policy() {
    this.navCtrl.push('PolicyPage');
  }

  feedback() {
    this.navCtrl.push('FeedbackPage');
  }

  openQRCode() {
    this.navCtrl.push('GenerateQrCodePage');
  }

  openPortDevice() {
    this.navCtrl.push('PortDevicePage');
  }

}
