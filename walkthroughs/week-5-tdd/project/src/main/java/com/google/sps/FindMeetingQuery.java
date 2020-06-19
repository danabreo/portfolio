// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;

public final class FindMeetingQuery {

  private static boolean attendanceRequired(Event event, MeetingRequest request) {
     Set<String> s = event.getAttendees();
     Collection<String> c = request.getAttendees();
     for (String person : c) {
       if (s.contains(person)){ return true; }
     }
     return false;
  }

  /**
   * Returns a list of times that are compatible with a given list of events
   * @param {Collection<Event>} events A list of events where each event contains
   * information about when a specific group of people are meeting.
   * @param {MeetingRequest} request An object describing the meeting being requested.
   * @return {Collection<TimeRange>} A list of meeting times where all required 
   * attendees are available
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Make two sorted lists, based on meeting start and end time.
    List<TimeRange> busyTimesStart = new ArrayList<TimeRange>();
    List<TimeRange> busyTimesEnd = new ArrayList<TimeRange>();
    for (Event event : events) {
      if (attendanceRequired(event, request)){
        busyTimesStart.add(event.getWhen());
        busyTimesEnd.add(event.getWhen());
      }
    }
    Collections.sort(busyTimesStart, TimeRange.ORDER_BY_START);
    Collections.sort(busyTimesEnd, TimeRange.ORDER_BY_END);

    // Store a list of times when no attendees are busy
    List<TimeRange> allAvailableTimes = new ArrayList<TimeRange>();
    
    // Return empty list if requested event is longer than the whole day
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) { return allAvailableTimes; }

    // If no required attendees are busy, return the whole day for availability
    int totalEvents = busyTimesStart.size();
    if (totalEvents == 0) {
      allAvailableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
      return allAvailableTimes;
    }

    // Check for free time before first scheduled event.
    int first = 0;
    if (busyTimesStart.get(first).start() > TimeRange.START_OF_DAY) {
      allAvailableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busyTimesStart.get(first).start(), false));
    }

    int curStartIndex = 0, curEndIndex = 1, lastEventIndex = totalEvents - 1;

    while (curStartIndex < totalEvents && curEndIndex < totalEvents) {
      while (curEndIndex < lastEventIndex && busyTimesStart.get(curStartIndex).overlaps(busyTimesEnd.get(curEndIndex))) {
        ++curEndIndex;
      }

      if (curEndIndex == lastEventIndex) { 
        if (busyTimesStart.get(curStartIndex).end() < busyTimesEnd.get(curEndIndex).start()) {
          allAvailableTimes.add(TimeRange.fromStartEnd(busyTimesStart.get(curStartIndex).end(), busyTimesEnd.get(curEndIndex).start(), false));
        }
        break;   
      }

      allAvailableTimes.add(TimeRange.fromStartEnd(busyTimesStart.get(curStartIndex).end(), busyTimesEnd.get(curEndIndex).start(), false));
      
      ++curStartIndex;
      while(curStartIndex < lastEventIndex && busyTimesStart.get(curStartIndex).start() < busyTimesEnd.get(curEndIndex).start()) {
        ++curStartIndex;
      }
    }

    // Check for free time after last scheduled event.
    int last = totalEvents-1;
    if (busyTimesEnd.get(last).end() < TimeRange.END_OF_DAY) {
      int latest = Math.max(busyTimesStart.get(last).end(), busyTimesEnd.get(last).end());
      allAvailableTimes.add(TimeRange.fromStartEnd(latest, TimeRange.END_OF_DAY, true));
    }

    // Only return available times that are long enough for the requested duration.
    List<TimeRange> availableTimes = new ArrayList<TimeRange>();
    for (TimeRange time : allAvailableTimes) {
      if (request.getDuration() <= time.duration()) { availableTimes.add(time); }
    }

    return availableTimes;
  }
}
