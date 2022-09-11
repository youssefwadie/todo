export class TodoItem {
  id: number;
  title: string;
  description: string;
  deadline: Date;
  done = false;


  constructor(id?: number, title?: string, description?: string, deadline?: Date, done?: boolean) {
    if (id) {
      this.id = id;
    }
    if (title) {
      this.title = title;
    }
    if (description) {
      this.description = description;
    }
    if (deadline) {
      this.deadline = deadline;
    }
    if (done) {
      this.done = done;
    }
  }
}
