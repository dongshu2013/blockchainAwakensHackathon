import {Component, ViewChild} from '@angular/core';
import {IonicPage, NavController, NavParams, Slides} from 'ionic-angular';
import {TabsPage} from "../tabs/tabs";

/**
 * Generated class for the IntroPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-intro',
  templateUrl: 'intro.html',
})
export class IntroPage {
  @ViewChild(Slides) slides: Slides;
  public steps: number = 1;

  constructor(public navCtrl: NavController) {

  }

  goToHome() {
    this.navCtrl.setRoot('Tabs');
  }

  buttomClick() {
    this.slides.slideTo(this.steps, 500);
    if(this.steps==4){
      this.navCtrl.setRoot(TabsPage);
    }
    this.steps = this.steps + 1;
  }

}
