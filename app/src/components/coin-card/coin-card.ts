import {Component, Input} from '@angular/core';
import {RestProvider} from "../../providers/rest/rest";
import {Events, NavController} from "ionic-angular";
import {CoinViewPage} from "../../pages/coin-view/coin-view";

/**
 * Generated class for the CoinCardComponent component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
  selector: 'coin-card',
  templateUrl: 'coin-card.html'
})
export class CoinCardComponent {
  @Input('n') n: any;
  @Input('currentTimeRange') currentTimeRange: any;
  @Input('color') color: any;
  @Input('selectedCurrency') selectedCurrency: any;
  @Input('currentLayout') currentLayout: any;

  public options: any;

  constructor(public navCtrl: NavController, public authService: RestProvider, public events: Events) {

    this.renderChart();
  }

  renderChart() {

    let backColor = '344152';
    const theme = this.authService.getTheme();
    if (this.authService.getTheme() === 'night-theme') {
      backColor = '#000';

    } else if (this.authService.getTheme() === 'light-theme') {
      backColor = '#fff';
    }

    let data = []
    for (let i = 0; i < 5; i++) {
      data.push(i * 20)
    }
    this.options = {
      chart: {
        type: 'sparkline',
        backgroundColor: backColor
      },
      series: [{
        data: data
      }]
    };

  }

  coinView() {
    let item: any = {};
    item.name = this.n.name;
    item.symbol = this.n.symbol
    this.navCtrl.push('CoinViewPage', {
      item: this.n
    })
  }

  errorLoad(e) {

  }
}
