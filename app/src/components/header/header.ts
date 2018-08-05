import {Component, Input} from '@angular/core';
import {PopoverController} from "ionic-angular";
import {PopoverPage} from "../../pages/popover/popover";
import {RestProvider} from "../../providers/rest/rest";

/**
 * Generated class for the HeaderComponent component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
  selector: 'app-header',
  templateUrl: 'header.html'
})
export class HeaderComponent {

  @Input('showText') showText;
  @Input('text') text;
  @Input('subtext') subtext;
  @Input('isCurrency') isCurrency;
  @Input('showButtons') showButtons;
  public currentCurrency = 'USD';

  constructor(public popoverCtrl: PopoverController,public authService: RestProvider) {
    this.currentCurrency = this.authService.getCurrency();
  }

  popOver(e)
  {
    let popover = this.popoverCtrl.create(PopoverPage);
    popover.present({
      ev: e
    });
  }
}
