import {Injectable} from '@angular/core';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';
import {User} from '../../model/user';
import 'rxjs/add/operator/toPromise'
import {HttpClient} from "@angular/common/http";
import {Storage} from '@ionic/storage';

@Injectable()
export class RestProvider {

  apiUrl = '';//http://walletalarmapi-env.iuic8r3ihf.us-west-1.elasticbeanstalk.com';
  public user: User;
  public bucket: any;
  public ipAddress: any;
  public AWSService: any;
  public urls: Array<any> = [];


  constructor(private http: HttpClient, public storage: Storage) {
    // this.user = new User();
    // this.user.userId = 1;
     //this.logout();
  }

  loadURL() {

    this.loadUserCredentials();

    return new Promise(resolve => {
      if (this.apiUrl.length > 0) {
        resolve(true);
        return;
      }
      this.AWSService = (<any>window).AWS;

      this.AWSService.config.accessKeyId = 'AKIAIMIOBALWCOG37JRQ';
      this.AWSService.config.secretAccessKey = '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';
      this.bucket = new this.AWSService.S3({params: {Bucket: 'walletalarm-images'}});

      let urlParams = {Bucket: 'walletalarm-images', Key: 'app.json'};
      let _self = this;
      this.bucket.getSignedUrl('getObject', urlParams, function (err, url) {
        let xobj = new XMLHttpRequest();
        xobj.overrideMimeType("application/json");
        xobj.open('GET', url, true);
        xobj.onreadystatechange = (() => {
          if (xobj.responseText.length > 0) {
            _self.apiUrl = JSON.parse(xobj.responseText).serverurl;
            resolve(true);
          } else {
            _self.apiUrl = 'http://walletalarmapi-env.iuic8r3ihf.us-west-1.elasticbeanstalk.com';
          }
        });
        xobj.send(null);
      })
    });
  }

  loadWebURL() {
    this.user = new User();
    this.apiUrl = 'http://walletalarmapi-env.iuic8r3ihf.us-west-1.elasticbeanstalk.com'
  }

  loginOnly() {

    let loginData = {
      loginId: 0,
      userId: this.user.userId,
      ipAddress: this.ipAddress
    }
    this.http.post(this.apiUrl + '/logins', loginData);
  }

  login(userId) {
    this.getIP().subscribe((data: any) => {
      if (data && data.ip) {
        this.ipAddress = data.ip;
        let loginData = {
          loginId: 0,
          userId: userId,
          ipAddress: data.ip
        };
        this.http.post(this.apiUrl + '/logins', loginData).subscribe(data => {

        }, err => {

        });
      }
    }, err => {
      this.login2(userId);
    });
  }

  login2(userId) {
    this.getIP2().subscribe((data: any) => {
      if (data && data.ip) {
        this.ipAddress = data.ip;
        let loginData = {
          loginId: 0,
          userId: userId,
          ipAddress: data.ip
        };
        this.http.post(this.apiUrl + '/logins', loginData).subscribe(data => {

        }, err => {

        });
      }
    }, err => {

      let loginData = {
        loginId: 0,
        userId: userId,
        ipAddress: 0
      };
      this.http.post(this.apiUrl + '/logins', loginData).subscribe(data => {

      });
    });
  }

  getIP() {
    return this.http.get('https://api.ipify.org/?format=json')
      .map(
        (res: Response) =>
          res.json()
      );
  }

  getIP2() {
    return this.http.get('http://bot.whatismyipaddress.com')
      .map(
        (res: Response) =>
          res.json()
      );
  }

  checkVersion(version) {
    return new Promise(resolve => {
      return this.http.get(this.apiUrl + '/versions?currentVersion=' + version).subscribe(data => {
        resolve(true);
      }, err => {
        if (err.status == 426)
          resolve(false);
        else
          resolve(true);
      });
    });
  }

  updateToken(userId, token, version) {
    this.login(userId);
    this.user = new User();
    this.user.userId = userId;
    this.user.notificationCode = token;
    this.user.version = version;
    this.user.isActive = true;
    this.storeUserCredentials();

    return new Promise(resolve => {
      this.http.put(this.apiUrl + '/users', this.user).subscribe(data => {
        resolve(true);
      }, err => {
        alert(err);
        resolve(false);
      });
    });
  }

  getUser(deviceId) {
    return this.http.get(this.apiUrl + '/users?deviceId=' + deviceId);

  }

  getUserByToken(token) {
    return this.http.get(this.apiUrl + '/users/notificationCode/' + token);
  }

  addUser(deviceId, token, os, version) {
    this.user = new User();
    this.user.deviceId = deviceId;
    this.user.userId = 0;
    this.user.os = os;
    this.user.version = version;
    this.user.notificationCode = token;
    this.user.timezone = "";
    return new Promise(resolve => {
      this.http.post(this.apiUrl + '/users', this.user).subscribe(data => {
        this.user.userId = data["userId"];
        try {
          this.login(this.user.userId);
        }
        catch (e) {
        }

        this.storeUserCredentials();
        resolve(true);

      }, err => {
        resolve(false);
      });
    });
  }

  storeUserCredentials() {
    this.storage.set('User', JSON.stringify(this.user));
  }

  loadUserCredentials() {
    return new Promise(resolve => {
      if (this.user && this.user.userId > 0)
        return resolve(true);
      this.storage.get('User').then((val) => {
        if (val && val.length > 0) {
          this.user = JSON.parse(val);
        } else {
          this.user = null;
        }
        resolve(true);
      });
    });
  }

  destroyUserCredentials() {
    this.storage.remove('User');
  }


  logout() {
    this.destroyUserCredentials();
  }

  feedback(msg) {
    let feedback = {
      "userId": this.user.userId,
      "message": msg,
    }

    return new Promise(resolve => {
      this.http.post(this.apiUrl + '/feedbacks', feedback).subscribe(data => {
        resolve(true);
      }, err => {
        resolve(false);
      });
    });
  }

  createPortfolio(name) {
    let portfolio = {
      portfolioId: 0,
      name: name,
      userId: this.user.userId,
      isActive: true
    };
    return new Promise(resolve => {
      this.http.post(this.apiUrl + '/portfolios', portfolio).subscribe(data => {
        console.log(data);
        resolve(data);
      }, err => {
        resolve(false);
      });
    });
  }

  getPortfolio() {
    return this.http.get(this.apiUrl + '/portfolios?userId=' + this.user.userId + '&offset=0&limit=1000');
  }

  getWallets() {
    return this.http.get(this.apiUrl + '/wallets?userId=' + this.user.userId + '&offset=0&limit=1000');
  }

  getWalletbyId(id) {
    return this.http.get(this.apiUrl + '/wallets/' + id);
  }

  deleteWallet(id) {
    return this.http.delete(this.apiUrl + '/wallets/' + id);
  }


  readWallet(url) {
    return this.http.get(url);
  }

  postWallet(address, payload, baseCurrency, blockchainType) {
    let wallet = {
      address: address,
      payload: payload,
      blockchainType: blockchainType,
      baseCurrency: baseCurrency
    };
    return new Promise(resolve => {
      this.http.post(this.apiUrl + '/wallets/tokens', wallet).subscribe(data => {
        resolve(data);
      }, err => {
        resolve(false);
      });
    });
  }


  createWallet(address, name, blockchainType, portfolioId) {
    let wallet = {
      walletId: 0,
      address: address,
      name: name,
      blockchainType: blockchainType,
      portfolioId: portfolioId,
      userId: this.user.userId,
      isActive: true
    };
    return new Promise(resolve => {
      this.http.post(this.apiUrl + '/wallets', wallet).subscribe(data => {
        resolve(data);
      }, err => {
        resolve(false);
      });
    });
  }

  getURL() {
    return new Promise(resolve => {
      if (this.urls.length > 0) {
        return resolve(true);
      }
      this.http.get(this.apiUrl + '/urls').subscribe((data: any) => {
        this.urls = data;
        resolve(true);
      });
    });
  }

  getURLByType(type) {
    let url = '';
    for (let i = 0; i < this.urls.length; i++) {
      if (this.urls[i].urlType == type) {
        url = this.urls[i].url;
        console.log(url);
        break;
      }
    }
    return url;
  }

  getCMCList(URL) {
    return this.http.get(URL);
  }

  setLanguage(code) {
    window.localStorage.setItem('LANGUAGE', code);
  }

  getLanguage() {
    return window.localStorage.getItem('LANGUAGE');
  }

  setCurrency(code) {
    window.localStorage.setItem('CURRENCY', code);
  }

  getCurrency() {
    return window.localStorage.getItem('CURRENCY');
  }

  setTheme(code) {
    window.localStorage.setItem('THEME', code);
  }

  setListStyle(code) {
    window.localStorage.setItem('LIST_STYLE', code);
  }

  getListStyle() {
    return window.localStorage.getItem('LIST_STYLE');
  }

  getTheme() {
    return window.localStorage.getItem('THEME');
  }

  getSavedWallet() {
    return window.localStorage.getItem('WalletData');
  }

  getPasscode() {
    return window.localStorage.getItem('PASSCODE');
  }

  savePasscode(code) {
    return window.localStorage.setItem('PASSCODE', code);
  }

  deletePasscode() {
    return window.localStorage.removeItem('PASSCODE');
  }

  getCMCChart(name, date) {
    let date_now = new Date().getTime();
    let url = ''
    if (date > 0)
      url = 'https://cors-anywhere.herokuapp.com/https://graphs2.coinmarketcap.com/currencies/' + name + '/' + date + '/' + date_now;
    else
      url = 'https://cors-anywhere.herokuapp.com/https://graphs2.coinmarketcap.com/currencies/' + name + '/' + date_now;
    return this.http.get(url);
  }

  clearWallet() {
    window.localStorage.removeItem('WalletData');
  }
}
