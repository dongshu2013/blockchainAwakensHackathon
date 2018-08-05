import {Component, EventEmitter, Output} from '@angular/core';
import {Events, IonicPage, ViewController} from 'ionic-angular';
import {HeaderComponent} from "../../components/header/header";
import {RestProvider} from "../../providers/rest/rest";

/**
 * Generated class for the PopoverPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */
@Component({
  template: `
    <ion-list [class]="selectedTheme">
      <button ion-item (click)="edit()">
        <ion-icon name="ios-albums-outline"></ion-icon>
        Switch Wallet
      </button>
      <button ion-item (click)="edit()">
        <ion-icon name="md-create"></ion-icon>
        Edit Wallet
      </button>
      <button ion-item (click)="delete()">
        <ion-icon name="trash"></ion-icon>
        Delete Wallet
      </button>
      <button ion-item (click)="refresh()">
        <ion-icon name="refresh"></ion-icon>
        Refresh Wallet
      </button>
    </ion-list>
  `
})
export class PopoverPage {
  selectedTheme: String;
  //@Output() onEdit = new EventEmitter<string>();

  constructor(public viewCtrl: ViewController,
              public events: Events,) {
  }

  edit() {
    //this.onEdit.next();
    this.events.publish('wallet:edit');
    this.viewCtrl.dismiss();
  }

  delete() {
    this.events.publish('wallet:delete');
    this.viewCtrl.dismiss();
  }

  refresh() {
    this.events.publish('wallet:refresh');
    this.viewCtrl.dismiss();
  }

}
