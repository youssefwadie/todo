export abstract class AppConstants {
  static localArguments: Intl.LocalesArgument = 'en-US';
  static dateTimeFormatOptions: Intl.DateTimeFormatOptions = {
    month: 'numeric',
    day: 'numeric',
    hour: "numeric",
    minute: "numeric"
  }
}
