export class Ico {
  public id: any;
  public title: string;
  public desc: string;
  public imageUrl: string
  public start: string;
  public end: string;
  public cap: number;
  public goal: number;
  public startIn: string;
  public isHidden: boolean;
  public collections: number;
  public percentComplete: number;

  constructor() {
    this.title = "";
    this.desc = "";
    this.start = "";
    this.end = "";
    this.goal = 0;
    this.isHidden =true;
  }
}
