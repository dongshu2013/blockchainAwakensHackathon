import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';

/**
 * Generated class for the GenerateQrCodePage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-generate-qr-code',
  templateUrl: 'generate-qr-code.html',
})
export class GenerateQrCodePage {

  elementType : 'url' | 'canvas' | 'img' = 'url';
  value : string = 'fdhfdh-adasd-asdsadsd-asdsadsad';

  constructor(public navCtrl: NavController, public navParams: NavParams) {
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad GenerateQrCodePage');
  }

}
