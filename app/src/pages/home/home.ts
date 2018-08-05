import {Component, NgZone, ViewChild} from '@angular/core';
import {NavController, Events, ToastController, AlertController, LoadingController, Content} from 'ionic-angular';
import {ThemeProvider} from "../../providers/theme/theme";
import {WalletIntroPage} from "../wallet-intro/wallet-intro";
import {RestProvider} from "../../providers/rest/rest";

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {

  @ViewChild(Content) content: Content;
  public currentTheme = 'theme-dark';
  public currentCurrency = 'USD';
  public currentLayout = 'card';
  public currentTimeRange = 1;
  public currencyAddressURL: String;
  public currencyPortfolioDetails: any = {};
  public currentWallet: any;
  public walletId = 0;
  public isCurrency = true;
  public switchEnable = false;
  public selectedWalletId = 0;
  public walletName = '';


  subHeader: boolean = false;
  public rawCoins: Array<any> = [];
  public isLoadingDone: boolean = false;
  public options: any;
  public url = '';
  public walletRawData: any;
  public blockchainType: String;
  public totalPrice: number;
  public walletLists = [];
  public colors = [
    '#F7CC58',
    '#73CDA7',
    '#EA594D',
    '#96A4FC',
    '#CE77B6',
    '#FF8769',
    '#57D0DA',
    '#536CFF',
    '#26C7DA',
    '#ff0fff'
  ];

  constructor(public navCtrl: NavController,
              public events: Events,
              public toastCtrl: ToastController,
              private alertCtrl: AlertController,
              public loadingCtrl: LoadingController,
              private _ngZone: NgZone,
              public authService: RestProvider,
              private themeProvider: ThemeProvider) {

    this.initApp();
    this.subscribeEvents();
  }

  ngOnDestroy() {
    this.events.unsubscribe('layout:change');
    this.events.unsubscribe('wallet:edit');
    this.events.unsubscribe('wallet:delete');
    this.events.unsubscribe('wallet:refresh');
  }


  subscribeEvents() {
    this.events.subscribe('layout:change', (layout) => {
      this.currentLayout = layout;
    });

    this.events.subscribe('wallet:edit', () => {

    });
    this.events.subscribe('wallet:delete', () => {
      this.deleteWallet();
    });
    this.events.subscribe('wallet:refresh', () => {
      this.reloadWalletData();
    });

    this.events.subscribe('currency:change', (currency) => {
      this.currentCurrency = currency;
      this.readWallet().then(() => {
        this.postWallet().then(() => {
          this.loadCoins();
        });
      });
    });
  }

  initApp() {
    this.themeProvider.getActiveTheme().subscribe(val => this.currentTheme = val.toString());
    this.url = this.authService.getURLByType('TOKENS');
    this.currentCurrency = this.authService.getCurrency();
    let data: any = this.authService.getSavedWallet();
    this.currentLayout = this.authService.getListStyle();

    if (data && data.length) {
      data = JSON.parse(data);
      this.selectedWalletId = data.selectedWalletId;
      this.walletLists = data.walletList;
      this.blockchainType = data.blockchainType;
      this.rawCoins = data.rawCoins;
      this.walletName = data.walletName;
      this.loadCoins();

    } else {
      this.reloadWalletData();
    }
  }


  reloadWalletData() {
    let loading = this.loadingCtrl.create({
      content: ''
    });
    loading.present();
    this.walletRawData = null;
    this.authService.clearWallet();
    this.authService.loadUserCredentials().then(() => {
      this.authService.getURL().then(() => {
        this.url = this.authService.getURLByType('TOKENS');
        this.getWallets().then((data) => {
          if (data) {
            this.readWallet().then(() => {
              this.postWallet().then(() => {
                this.loadCoins();
                loading.dismiss();
              });
            });
          } else {
            this.walletRawData = null;
            this.rawCoins = [];
            loading.dismiss();
          }
        })
      });
    })
  }

  async deleteWallet() {
    let alertC = this.alertCtrl.create({
      title: 'Confirm delete',
      message: 'Do you want to delete this wallet?',
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          handler: () => {
            console.log('Cancel clicked');
          }
        },
        {
          text: 'Yes',
          handler: () => {

            this.authService.deleteWallet(this.walletId).subscribe(() => {
              this.reloadWalletData();
            }, error1 => {
              alert(error1);
            })
          }
        }
      ]
    });
    await alertC.present();
  }

  ngOnInit() {
    this.events.subscribe('header:change', (state) => {
      this._ngZone.run(() => {
        this.subHeader = state;
      });
    });
    this.events.subscribe('chart:load', (state) => {
      this.renderCoinDistributionList();
    });

    this.events.subscribe('curency:change', (state) => {

    });
  }

  reLoadData(refresher) {
    this.loadCoins();
    setTimeout(() => {
      refresher.complete();
    }, 1000)

  }

  swipe(e) {
    if (e.direction === 2) {
      this.navCtrl.parent.select(1);
    }
  }

  chipClicked(type) {
    this.currentTimeRange = type;
  }

  buttomClick() {
    this.subHeader = !this.subHeader;
  }

  toggleAppTheme() {
    if (this.currentTheme === 'dark-theme') {
      this.themeProvider.setActiveTheme('light-theme');
    } else {
      this.themeProvider.setActiveTheme('dark-theme');
    }
  }

  getWallets() {
    return new Promise(resolve => {
      this.authService.getWallets().subscribe((data: any) => {
        if (data.length) {
          this.walletLists = data;
          this.setSelectedWallet(data);

          resolve(true);
        } else {
          this.isLoadingDone = true;
          resolve(false);
        }
      }, err => {
        this.isLoadingDone = true;
        this.presentToast('Error while loading wallet. Please pull down to refresh');
        resolve(false);
      });
    });
  }

  setSelectedWallet(data) {
    let index = 0;
    if (this.selectedWalletId > 0) {
      for (let i = 0; i < data.length; i++) {
        if (data[i].walletId == this.selectedWalletId) {
          index = i;
          break;
        }
      }
    } else {
      this.selectedWalletId = data[index].walletId;
    }
    this.currentWallet = data[index].address;
    this.walletId = data[index].walletId;
    this.blockchainType = data[index].blockchainType;
    this.walletName = data[index].name;
  }

  readWallet() {
    return new Promise(resolve => {
      this.currencyAddressURL = this.url.replace('address', this.currentWallet);
      this.authService.readWallet(this.currencyAddressURL).subscribe((data: any) => {
        if (data) {
          this.walletRawData = data;
          resolve(true);
        }
      }, err => {
        resolve(true);
        this.isLoadingDone = true;
        this.presentToast('Error while processing Wallet. Please pull down to refresh');
      });
    });
  }

  postWallet() {
    return new Promise(resolve => {
      this.authService.postWallet(this.currencyAddressURL, JSON.stringify(this.walletRawData), this.currentCurrency, this.blockchainType).then((data: Array<any>) => {
        if (data) {
          this.rawCoins = data;
          let saveD: any = {}
          if (typeof(this.currentWallet) != 'undefined') {
            saveD.selectedWalletId = this.selectedWalletId;
            saveD.walletName = this.walletName;
            saveD.currencyAddressURL = this.currentWallet;
            saveD.currentCurrency = this.currentCurrency;
            saveD.blockchainType = this.blockchainType;
            saveD.walletList = this.walletLists;
            //
            // saveD[this.currentWallet].walletRawData = this.walletRawData;
            saveD.rawCoins = this.rawCoins;
            window.localStorage.setItem('WalletData', JSON.stringify(saveD));
          }
          resolve(true);
        } else {
          this.isLoadingDone = true;
          this.presentToast('We are facing issue while loading wallet. Please try again later');
          resolve(false);
        }
      });
    });
  }

  open() {
    this.navCtrl.push('WalletIntroPage');
  }

  addWallet() {
    this.navCtrl.push('AddWalletPage');
  }

  presentToast(msg) {
    let toast = this.toastCtrl.create({
      message: msg,
      duration: 10000,
      position: 'bottom'
    });
    toast.present();
  }

  loadCoins() {
    let count = 0;
    this.totalPrice = 0;
    this.currencyPortfolioDetails.coinBalance = [];
    for (let i = 0; i < this.rawCoins.length; i++) {
      this.rawCoins[i].coin = {};
      if (this.rawCoins[i].cmcUrl != null) {
        let finalURL = this.rawCoins[i].cmcUrl;
        this.authService.getCMCList(finalURL).subscribe((cmc: any) => {
          count++
          cmc = cmc.data;
          if (cmc.id == 1) {
            cmc.fullURL = "https://s3-us-west-1.amazonaws.com/icoalarm-images/bitcoin.png";
          }
          else if (cmc.id == 2) {
            cmc.fullURL = "https://s3-us-west-1.amazonaws.com/icoalarm-images/litecoin.png";
          }
          else {
            cmc.fullURL = "https://s2.coinmarketcap.com/static/img/coins/32x32/" + cmc.id.toString() + ".png";
          }
          cmc.price = cmc.quotes[this.currentCurrency].price;
          cmc.balance = this.rawCoins[i].balance;
          cmc.totalPrice = 0;
          cmc.totalPrice = (cmc.balance) * (cmc.quotes[this.currentCurrency].price);
          cmc.difference1 = (cmc.price * cmc.quotes[this.currentCurrency].percent_change_1h)
          cmc.difference24 = (cmc.price * cmc.quotes[this.currentCurrency].percent_change_24h);
          cmc.difference7D = (cmc.price * cmc.quotes[this.currentCurrency].percent_change_7d);
          cmc.percent_change_1h = cmc.quotes[this.currentCurrency].percent_change_7d;
          cmc.percent_change_24h = cmc.quotes[this.currentCurrency].percent_change_24h;
          cmc.percent_change_7d = cmc.quotes[this.currentCurrency].percent_change_7d;

          cmc.market_cap = cmc.quotes[this.currentCurrency].market_cap;
          cmc.volume_24h = cmc.quotes[this.currentCurrency].volume_24h;
          this.totalPrice += cmc.totalPrice;
          cmc.color = '5px solid ' + this.colors[this.currencyPortfolioDetails.coinBalance.length];
          this.rawCoins[i].coin = cmc;
          this.currencyPortfolioDetails.coinBalance.push({name: cmc.symbol, y: cmc.totalPrice});

          this.renderCoinDistributionList();
        });
      } else {
        count++
        let coin: any = {};
        coin.balance = this.rawCoins[i].balance;
        coin.totalPrice = 0;
        coin.name = this.rawCoins[i].name;
        coin.symbol = this.rawCoins[i].symbol;
        this.rawCoins[i].coin = coin;
        this.renderCoinDistributionList();
      }

    }
  }

  renderCoinDistributionList() {

    let backColor = '#243142';
    const theme = this.authService.getTheme();
    if (this.currentTheme === 'night-theme') {
      backColor = '#000';

    } else if (this.currentTheme === 'light-theme') {
      backColor = '#fff';
    }

    let data = []
    for (let i = 0; i < 5; i++) {
      data.push({name: 'Ship', y: 20})
    }
    this.options = {
      chart: {
        type: 'pie',
        backgroundColor: backColor
      },
      title: {
        verticalAlign: 'middle',
        floating: true,
        text: '$' + this.totalPrice.toFixed(2) + '<br>5.5%',
        style: {"color": "#fff", "fontSize": "26px"}
      },
      colors: this.colors,
      plotOptions: {
        pie: {
          innerSize: '80%',
          // startAngle: -90,
          // endAngle: 90,
          dataLabels: {
            enabled: false
          }
        }
      },
      series: [{
        data: this.currencyPortfolioDetails.coinBalance
      }]
    };
    if (this.content)
      this.content.resize();
  }

  switchWallet() {
    this.switchEnable = true;
  }

  selectWallet(e) {
    this.switchEnable = false;
    this.selectedWalletId = e;
    this.reloadWalletData();
  }
}
