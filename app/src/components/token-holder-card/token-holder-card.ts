import {Component, Input} from '@angular/core';

/**
 * Generated class for the TokenHolderCardComponent component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
  selector: 'token-holder-card',
  templateUrl: 'token-holder-card.html'
})
export class TokenHolderCardComponent {

  @Input('holders') holders: any;
  @Input('currentLayout') currentLayout: any;

  constructor() {
  }

}
