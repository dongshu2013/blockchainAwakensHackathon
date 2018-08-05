import {Component, NgZone} from '@angular/core';
import {AlertController, Events, IonicPage, NavController, NavParams, ToastController} from 'ionic-angular';
import {QRScanner, QRScannerStatus} from "@ionic-native/qr-scanner";
import {Clipboard} from "@ionic-native/clipboard";
import {RestProvider} from "../../providers/rest/rest";
import {TranslateService} from "@ngx-translate/core";

/**
 * Generated class for the AddWalletPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-add-wallet',
  templateUrl: 'add-wallet.html',
})
export class AddWalletPage {

  public walletAddress: string = '';
  public nickName = '';
  public  walletType = 'ETH';
  themeSegment = 'dark-theme';
  selectOptions:any;
  public portfolio: number;
  portfolios: any = [{
    portfolioId: '-1',
    name: 'Create new'
  }];

  constructor(public navCtrl: NavController,
              public translate: TranslateService,
              private qrScanner: QRScanner,
              public authService: RestProvider,
              public events: Events,
              private clipboard: Clipboard,
              private alertCtrl: AlertController,
              private _ngZone: NgZone,
              public toastCtrl: ToastController) {
    this.authService.loadURL();
    this.getPortfolios();
    let code = window.localStorage.getItem('LANGUAGE');
    this.translate.setDefaultLang(code);
    this.themeSegment =this.authService.getTheme();

    this.selectOptions = {
      title: 'Chose Wallet',
      subTitle: 'More wallet type coming soon',
      cssClass: this.themeSegment
    };
  }


  ionViewDidLoad() {
    console.log('ionViewDidLoad AddWalletPage');
  }

  portSelect() {
    if( this.portfolio === -1) {
      this.presentAlert();
    }
  }

  async presentAlert() {
    const alert = await this.alertCtrl.create({
      title: 'Create Portfolio',
      enableBackdropDismiss: false,
      inputs: [
        {
          name: 'portName',
          type: 'text',
          placeholder: 'Portfolio Name'
        }
      ],
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => {
            console.log('Confirm Cancel')
          }
        }, {
          text: 'Save',
          handler: data => {
            if(data.portName.trim().length === 0){
              return false;
            }
            this.createPortfolio(data.portName);
          }
        }
      ]
    });

    await alert.present();
  }


  paste() {
    this.clipboard.paste().then(
      (resolve: string) => {
        alert(resolve);
        this.walletAddress = resolve;
      },
      (reject: string) => {
        alert('Error: ' + reject);
      }
    );
  }


  scan() {

    var context = this;
    // Optionally request the permission early
    this.qrScanner.prepare()
      .then((status: QRScannerStatus) => {

        if (status.authorized) {

          var ionApp = <HTMLElement>document.getElementsByTagName("ion-app")[0];
          let scanSub = this.qrScanner.scan().subscribe((scannedAddress: string) => {

            this._ngZone.run(() => {
              this.walletAddress = scannedAddress;
            });
            this.qrScanner.hide(); // hide camera preview
            scanSub.unsubscribe(); // stop scanning
            ionApp.style.display = "block";
          });

          // show camera preview
          ionApp.style.display = "none";
          context.qrScanner.show();
          // setTimeout(() => {
          //   ionApp.style.display = "block";
          //   scanSub.unsubscribe(); // stop scanning
          //   context.qrScanner.hide();
          // }, 10000);
          // wait for user to scan something, then the observable callback will be called

        } else if (status.denied) {
          this.presentToast("Denied permission to access camera");
        } else {
          this.presentToast("Something else is happening with the camera");
        }
      })
      .catch((e: any) => console.log('Error is', e));
  }

  save() {
    if (this.nickName.trim().length == 0) {
      this.nickName  ='My Wallet';
    }
    if (this.walletAddress.trim().length == 0) {
      this.presentToast('Please enter wallet address');
      return;
    }
    this.createWallet();
  }

  getPortfolios() {
    this.authService.getPortfolio().subscribe((data: any) => {
      if(data.length) {
        this.portfolios = data;
        this.portfolio = data[0]['portfolioId'];
        this.portfolios.splice(0, 0, { portfolioId: -1, name: 'Create new'});
      } else {
        this.createPortfolio('My Portfolio');
      }
    }, err => {
      this.presentToast('Error while loading ICO. Please pull down to refresh');
    });
  }

  createPortfolio(name){
    this.authService.createPortfolio(name).then(data => {
      if (data) {
        this.portfolio = data['portfolioId'];
        //this.getPortfolios();
      } else {
        this.presentToast('We are facing issue while submitting feedback at the moment. Please try again after sometime. You can also contact us on support@IcoAlarmApp.com');
      }
    });
  }

  createWallet(){
    this.authService.createWallet(this.walletAddress, this.nickName, 'ETH', this.portfolio).then(data => {
      if (data) {
        this.navCtrl.popToRoot();
        this.events.publish('wallet:refresh');
        this.presentToast('Your new Wallet has been created. We are now scanning the wallet.');
      } else {
        this.presentToast('We are facing issue while registering a new wallet. Please try again later');
        this.navCtrl.popToRoot();
      }
    });
  }


  presentToast(msg) {
    let toast = this.toastCtrl.create({
      message: msg,
      duration: 10000,
      position: 'bottom'
    });
    toast.present();
  }
}
