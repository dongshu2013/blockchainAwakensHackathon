import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {Slides} from "ionic-angular";

/**
 * Generated class for the CardSwitchComponent component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
  selector: 'card-switch',
  templateUrl: 'card-switch.html'
})
export class CardSwitchComponent {

  @ViewChild(Slides) slides: Slides;
  @Input('walletList') walletList: any;
  @Output() onSelect = new EventEmitter<string>();


  constructor() {
  }

  cardClick(id){
    //alert(id);
    //this.slides.slideTo((id-1), 500);
    this.onSelect.next(id);
  }

}
