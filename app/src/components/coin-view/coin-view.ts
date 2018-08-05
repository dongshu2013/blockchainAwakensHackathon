import { Component } from '@angular/core';
import {
  AlertController,
  Events,
  IonicPage,
  LoadingController,
  NavController,
  NavParams,
  Platform,
  ToastController
} from 'ionic-angular';
import {RestProvider} from "../../providers/rest/rest";
import {SocialSharing} from "@ionic-native/social-sharing";
import {Push} from "@ionic-native/push";
import {ThemeProvider} from "../../providers/theme/theme";

/**
 * Generated class for the CoinViewPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-coin-view',
  templateUrl: 'coin-view.html',
})
export class CoinViewPage {

  public coin: any;
  public options: any;
  public chips: any;
  public newsSubscriptionId: number;
  public newsTagId:number;
  public segment: string = 'HOLDERS';
  public currentCurrency = 'USD';
  public currentTheme = 'theme-dark';
  public currentLayout = 'card';
  public transactions:any;
  public holders:any;


  constructor(
              public platform: Platform,
              public events: Events,
              public loadingCtrl: LoadingController,
              public authService: RestProvider,
              public navCtrl: NavController,
              private socialSharing: SocialSharing,
              public toastCtrl: ToastController,
              public push: Push,
              private themeProvider: ThemeProvider,
              public navParams: NavParams) {

    this.currentCurrency = this.authService.getCurrency();
    this.themeProvider.getActiveTheme().subscribe(val => this.currentTheme = val.toString());
    if(window.localStorage.getItem('COIN_DATA')){
      this.coin = JSON.parse(window.localStorage.getItem('COIN_DATA'));
      let chart = JSON.parse(window.localStorage.getItem('COIN_CHART'));
      this.renderChart(chart);
    } else{
      this.coin = navParams.data.item;
      window.localStorage.setItem('COIN_DATA',JSON.stringify(this.coin));
      let time = new Date().getTime() - this.getDaysOff(1);
      this.loadData(time);
    }

    this.getTransactions();
    this.getHolders();
    this.currentLayout = this.authService.getListStyle();


    // this.coin = navParams.data.item;
    // window.localStorage.setItem('COIN_DATA',JSON.stringify(this.coin));
    // let time = new Date().getTime() - this.getDaysOff(1);
    // this.loadData(time);


    this.chips = [
      {text: '1d', color: 'primary', day: 1},
      {text: '7d', color: 'gray', day: 7},
      {text: '1m', color: 'gray', day: 30},
      {text: '3m', color: 'gray', day: 90},
      {text: '1y', color: 'gray', day: 365},
      {text: 'All', color: 'gray', day: 0}
    ]
  }

  updateData(){

  }

  chipClick(c) {
    for (let i = 0; i < this.chips.length; i++) {
      if (this.chips[i].text == c.text) {
        this.chips[i].color = 'primary';
      } else {
        this.chips[i].color = 'gray';
      }
    }
    let time = new Date().getTime() - this.getDaysOff(c.day);
    this.loadData(time);
  }

  getDaysOff(day) {
    return (24 * 60 * 60 * 1000) * day;
  }

  fav() {
    let favs = JSON.parse(window.localStorage.getItem('COINS_FAV'));
    if(favs == null || typeof(favs) == 'undefined')
      favs = {};

    if (favs[this.coin.id]) {
      delete favs[this.coin.id];
      this.coin.class = 'star-outline';

    } else {
      favs[this.coin.id] = {
        id: this.coin.id,
        name: this.coin.name
      }
      this.coin.class = 'star';
    }
    this.events.publish('fav:load');
    window.localStorage.setItem('COINS_FAV', JSON.stringify(favs));
  }


  shareCoin() {

    let link = 'Source: http://www.icoalarmapp.com';
    let text = this.coin.name + " (" + this.coin.symbol + ") \n\n";
    text += "Rank: "+ this.coin.rank + "\n\n";
    text +="Price: $" + this.coin.quotes.USD.price + "\n\n";
    text += "Market Cap: "+ this.coin.quotes.USD.market_cap + "\n\n";
    text += "Circulating Supply: "+ this.coin.circulating_supply + "\n\n";
    text += "Total Supply: "+ this.coin.quotes.USD.market_cap + "\n\n";
    text += "Max Supply: "+ this.coin.quotes.USD.market_cap + "\n\n";
    text += "% change 1h: "+ this.coin.quotes.USD.percent_change_1h + "\n\n";
    text += "% change 24h: "+ this.coin.quotes.USD.percent_change_24h + "\n\n";
    text += "% change 7d: "+ this.coin.quotes.USD.percent_change_7d + "\n\n";

    text += link;

    this.socialSharing.share(text).then(() => {
      // Success!
    }).catch(() => {
      // Error!
    });
  }


  loadData(date) {
    let loading = this.loadingCtrl.create({
      content: 'Loading...'
    });
    loading.present();
    let name = this.coin.name.replace(' ', '-').replace(/ /g, '');
    return new Promise(resolve => {
      this.authService.getCMCChart(name, date).subscribe((cmc: any) => {
        window.localStorage.setItem('COIN_CHART',JSON.stringify(cmc));
        this.renderChart(cmc);
        loading.dismiss();
        resolve(true);
      }, err => {
        loading.dismiss();
        this.presentToast('Error while loading Coins. Please pull down to refresh');
        resolve(false);
      });
    });
  }


  renderChart(data) {
    let primary ='#243142';
    let secondary = '#344152';

    if (this.currentTheme === 'dark-theme') {
      primary = '#243142';
      secondary = '#344152'

    } else if (this.currentTheme === 'night-theme') {
      primary = '#111';
      secondary = '#333'
    }

    console.log(this.currentTheme);

    this.options = {
      chart: {
        zoomType: 'x',
        height: 250,
        width: this.platform.width(),
        backgroundColor: {
          linearGradient: {x1: 0, y1: 0, x2: 1, y2: 1},
          stops: [
            [0, primary],
            [1, secondary]
          ]
        },
        style: {
          fontFamily: '\'Unica One\', sans-serif'
        },
        plotBorderColor: '#606063'
      },
      xAxis: {
        type: 'datetime',
        gridLineWidth: 0,
      },
      yAxis: {
        title: {
          text: ''
        }
      },
      legend: {
        enabled: false
      },
      title: {
        text: ''
      },
      plotOptions: {
        area: {
          fillColor: {
            linearGradient: {
              x1: 0,
              y1: 0,
              x2: 1,
              y2: 1
            },
            stops: [
              [0, '#2b908f'],
              [1, 'rgba(43,144,143,0)']
            ]
          },
          threshold: null
        }
      },

      series: [{
        type: 'area',
        name: 'Price USD',
        lineColor: '#2B8F8E',
        lineWidth: 1,
        data: data.price_usd
      }]
    }
  }

  getTransactions(){
    this.transactions =[];
    for(let i=0;i<10;i++){
      this.transactions.push({index:(i+1), date: '2/2/2013', text:'Some text' });
    }
  }

  getHolders(){
    this.holders =[];
    for(let i=0;i<10;i++){
      this.holders.push({address: '0xb297cacf0f91c86dd9d2fb47c6d12783121ab780', quantity:1275880.949, percent: 65 });
    }
  }

  presentToast(msg) {
    let toast = this.toastCtrl.create({
      message: msg,
      duration: 5000,

      position: 'top'
    });
    toast.present();
  }


}
