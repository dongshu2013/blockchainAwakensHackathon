import {Component} from '@angular/core';
import {Events, IonicPage, NavController, NavParams} from 'ionic-angular';
import {RestProvider} from "../../providers/rest/rest";
import {TranslateService} from '@ngx-translate/core';

@IonicPage()
@Component({
  selector: 'page-pin-screen',
  templateUrl: 'pin-screen.html',
})
export class PinScreenPage {

  public text = 'Please enter 4-digit code';
  public totalDigit = 0;
  public pin = [];
  public retryPin = [];
  public currentLockStatus = 1;
  public currentPasscode = null;


  constructor(public navCtrl: NavController,
              public navParams: NavParams,
              public events: Events,
              public translate: TranslateService,
              public authService: RestProvider) {

    this.setLanguage();
    this.currentPasscode = this.authService.getPasscode();
    if (this.currentPasscode != null) {
      this.currentLockStatus = 3;
      this.translate.get('pin_msg1').subscribe((res: string) => {
        this.text = res;
      });
    }
  }

  setLanguage() {
    let language = this.authService.getLanguage();
    this.translate.setDefaultLang(language);
  }

  numberClick(type) {
    if (this.totalDigit <= 4) {
      this.totalDigit++;
      if (this.currentLockStatus !== 2) {
        this.pin.push(type);
      } else {
        this.retryPin.push(type);
      }
    }
    if (this.totalDigit === 4) {
      if (this.currentLockStatus === 1) {
        this.totalDigit = 0;
        this.translate.get('pin_msg2').subscribe((res: string) => {
          this.text = res;
        });
        this.currentLockStatus = 2;
      } else if (this.currentLockStatus === 2) {
        // Save passcode
        this.totalDigit = 0;
        if (this.pin.join(',') === this.retryPin.join(',')) {
          this.authService.savePasscode(this.pin.join(','));
          this.goBack();
        } else {
          this.totalDigit = 0;
          this.retryPin = [];
          this.translate.get('pin_msg3').subscribe((res: string) => {
            this.text = res;
          });
        }
      } else if (this.currentLockStatus === 3) {
        // disbale passcode and set it to null
        this.totalDigit = 0;
        if (this.pin.join(',') === this.currentPasscode) {
          this.authService.deletePasscode();
          this.goBack();
        } else {
          this.totalDigit = 0;
          this.pin = [];
          this.translate.get('pin_msg2').subscribe((res: string) => {
            this.text = res;
          });
        }
      }
    }
  }

  deleteClick() {
    if (this.totalDigit != 0) {
      this.totalDigit--;
      this.pin.pop();
    }
  }

  cancelClick() {
    this.goBack();
  }

  goBack() {
    this.events.publish('lock:change');
    this.navCtrl.pop();
  }
}
