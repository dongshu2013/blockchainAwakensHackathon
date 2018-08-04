import {Injectable} from '@angular/core';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';
import {User} from '../../model/user';
import 'rxjs/add/operator/toPromise'
import {HttpClient} from "@angular/common/http";
import {Storage} from '@ionic/storage';
import {ApplicationConstants} from "../../../applicationConstants";

@Injectable()
export class RestProvider {

  apiUrl = '';
  public user: User;
  public bucket: any;
  public ipAddress: any;
  public AWSService: any;
  public urls: Array<any> = [];


  constructor(private http: HttpClient, public storage: Storage) {
    this.user = new User();
  }

  loadURL() {
    this.loadUserCredentials();
    return new Promise(resolve => {
      let applicationConst = new ApplicationConstants();
      this.apiUrl = applicationConst.url;
    });
  }


  storeUserCredentials() {
    this.storage.set('User', JSON.stringify(this.user));
  }

  loadUserCredentials() {

    if (this.user && this.user.userId > 0)
      return;
    this.storage.get('User').then((val) => {
      if (val && val.length > 0) {
        this.user = JSON.parse(val);
        return this.user;
      } else {
        this.user = null;
      }
    });
  }

  destroyUserCredentials() {
    this.storage.remove('User');
  }

  logout() {
    this.destroyUserCredentials();
  }

  setTheme(code) {
    window.localStorage.setItem('THEME', code);
  }
}
