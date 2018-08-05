export class User {
  public userId:number;
  public deviceId: string;
  public notificationCode: string;
  public os: string;
  public version: string;
  public timezone: string;
  public isActive: boolean;
  public uuid:string;

  constructor(){
    this.userId = 1;
  }
}
