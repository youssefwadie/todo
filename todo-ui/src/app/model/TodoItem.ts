export class TodoItem {
  id: number;
  title: string;
  description: string;
  deadTime: Date;
  done = false;


  constructor(id: number | undefined, title: string | undefined, description: string | undefined, deadTime: Date | undefined) {
    if (id) {
      this.id = id;
    }
    if (title) {
      this.title = title;
    }
    if (description) {
      this.description = description;
    }
    if (deadTime) {
      this.deadTime = deadTime;
    }
  }
}
