export abstract class AppConstants {

  public static get LOCALES_ARGUMENT(): Intl.LocalesArgument {
    return 'en-US'
  };

  public static get DATE_TIME_FORMAT_OPTIONS(): Intl.DateTimeFormatOptions {
    return {
      month: 'numeric',
      day: 'numeric',
      hour: "numeric",
      minute: "numeric"
    }
  }

  public static get API_ACCOUNT_LOGIN_PATH() {
    return "users/login"
  }

  public static get API_TODO_LIST_PATH() {
    return "todo";
  }

  public static get API_TODO_LIST_FIND_BY_ID() {
    return 'todo/';
  }

  public static get API_UPDATE_TODO() {
    return 'todo/';
  }

  public static get API_ADD_TODO() {
    return "todo/";
  }

  // A bit weird naming style but ...!
  public static API_UPDATE_TODO_STATUS_PATH(id: number, status: boolean): string {
    return `todo/${id}/${status}`;
  }

  public static get API_REFRESH_TOKEN() {
    return 'users/refresh';
  }
}
