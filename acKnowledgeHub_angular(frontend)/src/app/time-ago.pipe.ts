import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeAgo'
})
export class TimeAgoPipe implements PipeTransform {

  transform(value: any): string {
    if (!value) {
      return 'Just now';
    }

    const now = new Date();
    const date = new Date(value);
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    let interval = Math.floor(seconds / 31536000); // seconds in a year

    if (interval >= 1) {
      return interval === 1 ? `${interval} year ago` : `${interval} years ago`;
    }

    interval = Math.floor(seconds / 2592000); // seconds in a month
    if (interval >= 1) {
      return interval === 1 ? `${interval} month ago` : `${interval} months ago`;
    }

    interval = Math.floor(seconds / 86400); // seconds in a day
    if (interval >= 1) {
      return interval === 1 ? `${interval} day ago` : `${interval} days ago`;
    }

    interval = Math.floor(seconds / 3600); // seconds in an hour
    if (interval >= 1) {
      return interval === 1 ? `${interval} hour ago` : `${interval} hours ago`;
    }

    interval = Math.floor(seconds / 60); // seconds in a minute
    if (interval >= 1) {
      return interval === 1 ? `${interval} minute ago` : `${interval} minutes ago`;
    }

    return seconds === 1 ? `${seconds} second ago` : `${seconds} seconds ago`;
  }

}
