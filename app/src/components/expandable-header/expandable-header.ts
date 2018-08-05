import { Component, Input, ElementRef, Renderer } from '@angular/core';
import {Events} from 'ionic-angular';


/**
 * Generated class for the ExpandableHeaderComponent component.
 *
 * See https://angular.io/api/core/Component for more info on Angular
 * Components.
 */
@Component({
  selector: 'expandable-header',
  templateUrl: 'expandable-header.html'
})
export class ExpandableHeaderComponent {

  @Input('scrollArea') scrollArea: any;
  @Input('headerHeight') headerHeight: number;
  public isHidden:boolean=false;

  newHeaderHeight: any;

  constructor(public element: ElementRef, public renderer: Renderer, public events: Events) {

  }

  ngOnInit(){

    this.renderer.setElementStyle(this.element.nativeElement, 'height', this.headerHeight + 'px');

    this.scrollArea.ionScroll.subscribe((ev) => {


      // if(ev.scrollTop >150){
      //   this.events.publish('header:change', true);
      //   this.isHidden = true;
      //   console.log(ev);
      //   return;
      // }

      this.resizeHeader(ev);
    });

  }

  resizeHeader(ev){

    ev.domWrite(() => {

      this.newHeaderHeight = this.headerHeight - ev.scrollTop;

      if(this.newHeaderHeight < 0){
        this.newHeaderHeight = 0;
        if(this.isHidden==false){
          this.events.publish('header:change', true);
          this.isHidden = true;
        }
      } else{
        if(this.isHidden==true) {
          this.events.publish('header:change', false);
          this.isHidden = false;
        }
      }

      this.renderer.setElementStyle(this.element.nativeElement, 'height', this.newHeaderHeight + 'px');

      for(let headerElement of this.element.nativeElement.children){

        let totalHeight = headerElement.offsetTop + headerElement.clientHeight;
        if (totalHeight <= 300){
          this.renderer.setElementStyle(headerElement, 'opacity', (totalHeight/200).toString());
        } else if(totalHeight > 200){
          this.renderer.setElementStyle(headerElement, 'opacity', '1');
        }
      }
    });
  }
}
