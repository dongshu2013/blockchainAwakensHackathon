import { Component } from '@angular/core';
import {Events, IonicPage, NavController, NavParams, ToastController} from 'ionic-angular';
import {RestProvider} from "../../providers/rest/rest";

@IonicPage()
@Component({
  selector: 'page-feedback',
  templateUrl: 'feedback.html',
})
export class FeedbackPage {

  public feedbackText: string;
  public disabled: boolean;
  public isFoucs: boolean;


  constructor(public navCtrl: NavController,
              public events: Events,
              public toastCtrl: ToastController,
              public authService: RestProvider) {
    this.authService.loadUserCredentials();
    this.disabled = false;
    this.isFoucs = true;
    this.feedbackText = '';
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad FeedbackPage');
  }

  ngOnInit() {
    if (this.isFoucs) {
      this.isFoucs = false;
      setTimeout(() => {
        let someElement = window.document.getElementById('si');
        someElement.focus();
      }, 1000);
    }
  }

  onInput(ev: any) {
    this.feedbackText = ev.target.value;
  }


  feedback() {
    if (this.feedbackText.trim().length == 0) {
      this.presentToast('Please type some message and click send button');
      return;
    }
    this.authService.feedback(this.feedbackText).then(data => {
      this.disabled = true;
      if (data) {
        this.presentToast('Thanks for writing to us. We will get back to you soon. You can also contact us on support@WalletAlarmApp.com');
      } else {
        this.presentToast('We are facing issue while submitting feedback at the moment. Please try again after sometime. You can also contact us on support@WalletAlarmApp.com');
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
