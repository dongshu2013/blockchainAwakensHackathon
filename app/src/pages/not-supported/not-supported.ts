import {Component} from '@angular/core';
import {RestProvider} from "../../providers/rest/rest";
import {Platform} from "ionic-angular";

@Component({
  selector: 'page-not-supported',
  templateUrl: 'not-supported.html',
})
export class NotSupported {

  constructor(public authService: RestProvider, public platform: Platform) {
    authService.destroyUserCredentials();
  }

  update() {
    if (this.platform.is('ios')) {
      window.open('https://itunes.apple.com/us/app/icoalarm/id1308014716', '_system');
    } else {
      window.open('market://details?id=com.icoalarm.app', '_system');
    }
  }
}
