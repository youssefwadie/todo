export class Todo {
  title: string;
  description: string;
  deadTime: Date;
  done = false;


  constructor(title: string, description: string, deadTime: Date) {
    this.title = title;
    this.description = description;
    this.deadTime = deadTime;
  }
}
