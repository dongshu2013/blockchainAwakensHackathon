import {Component, Input} from '@angular/core';

/**
 * Generated class for the NewsCardComponent component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
  selector: 'news-card',
  templateUrl: 'news-card.html'
})
export class NewsCardComponent {

  @Input('news') news: any;

  text: string;

  constructor() {
    this.text = 'Hello World';
  }

}
