import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';

/**
 * Generated class for the CoinWatchPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-coin-watch',
  templateUrl: 'coin-watch.html',
})
export class CoinWatchPage {

  public news =[
    {
      from: 'My Wallet',
      to: 'Other Wallet',
      val: 3,
      gas: '0.01ETH',
      balance: '200ETH'
    },
    {
      from: 'Victor',
      to: 'Mike',
      val: 4,
      gas: '0.05ETH',
      balance: '100ETH'
    },
    {
      from: 'Victor',
      to: 'Mike',
      gas: '0.05ETH',
      val: 1,
      balance: '100ETH'
    },
    {
      from: 'Victor',
      to: 'Mike',
      val: 5,
      gas: '0.05ETH',
      balance: '100ETH'
    },
    {
      from: 'Victor',
      to: 'Mike',
      val: 21,
      gas: '0.05ETH',
      balance: '100ETH'
    },
    {
      from: 'Victor',
      to: 'Mike',
      val: 3,
      gas: '0.05ETH',
      balance: '100ETH'
    },
    {
      from: 'Victor',
      to: 'Mike',
      val: 33,
      gas: '0.05ETH',
      balance: '100ETH'
    },
    {
      from: 'Victor',
      to: 'Mike',
      val: 34,
      gas: '0.05ETH',
      balance: '100ETH'
    }


  ]

  constructor(public navCtrl: NavController, public navParams: NavParams) {
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad CoinWatchPage');
  }

  swipe(event) {
    if(event.direction === 2) {
      this.navCtrl.parent.select(2);
    }
    if(event.direction === 4) {
      this.navCtrl.parent.select(0);
    }
  }

}
