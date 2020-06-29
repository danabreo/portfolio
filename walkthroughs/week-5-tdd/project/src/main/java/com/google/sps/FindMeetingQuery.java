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
import java.util.stream.Collectors;

public final class FindMeetingQuery {

  /** Checks if the participants of the request are also attending the given event. */
  private static boolean sharesAttendees(Event event, MeetingRequest request) {
    Set<String> eventAttendees = event.getAttendees();
    Collection<String> requestAttendees = request.getAttendees();
    for (String person : requestAttendees) {
      if (eventAttendees.contains(person)) return true;
    }
    return false;
  }

  /**
   * Returns a list of times that are compatible with a given list of events.
   * @param events A list of events where each event contains
   * information about when a specific group of people are meeting.
   * @param request An object describing the meeting being requested.
   * @return A list of meeting times where all attendees are available.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> sortedTimes = new ArrayList<TimeRange>();
    for (Event event : events) {
      if (sharesAttendees(event, request)) sortedTimes.add(event.getWhen());
    }
    Collections.sort(sortedTimes, TimeRange.ORDER_BY_START);

    List<TimeRange> allAvailableBlocks = new ArrayList<TimeRange>();
    
    /* Return empty list for an invalid request. */
    if (request.getDuration() >= TimeRange.WHOLE_DAY.duration()) return allAvailableBlocks;
    
    /* Return whole day if no events are scheduled. */ 
    else if (sortedTimes.size() == 0) {
      allAvailableBlocks.add(TimeRange.WHOLE_DAY);
      return allAvailableBlocks;
    }

    /* Combine overlapping events into 'busy' blocks of time. */
    List<TimeRange> allBusyBlocks = new ArrayList<TimeRange>();
    TimeRange currBlock = sortedTimes.get(0);
    TimeRange currMergedTime = sortedTimes.get(0);
    for (int i = 1; i < sortedTimes.size(); ++i) {
      currMergedTime = sortedTimes.get(i);
      if (!currMergedTime.overlaps(currBlock)) {
        allBusyBlocks.add(currBlock);
        currBlock = currMergedTime;
      } else if (currBlock.end() < currMergedTime.end()) {
        currBlock = TimeRange.fromStartEnd(currBlock.start(), currMergedTime.end(), false);
      }
    }
    allBusyBlocks.add(currBlock);
    
    /* Create a list of 'available' blocks of time based on 'busy' blocks. */

    /* Check for available time before first 'busy' block. */
    if (TimeRange.START_OF_DAY < allBusyBlocks.get(0).start()) {
      allAvailableBlocks.add(
        TimeRange.fromStartEnd(TimeRange.START_OF_DAY, allBusyBlocks.get(0).start(), false));
    }

    /* Check for available time between middle 'busy' block. */
    for (int i = 0; i < allBusyBlocks.size() - 1; ++i) {
      allAvailableBlocks.add(
        TimeRange.fromStartEnd(allBusyBlocks.get(i).end(), allBusyBlocks.get(i + 1).start(), false));
    }

    /* Check for available time after last 'busy' block. */
    if (allBusyBlocks.get(allBusyBlocks.size() - 1).end() < TimeRange.END_OF_DAY) {
      allAvailableBlocks.add(
        TimeRange.fromStartEnd(allBusyBlocks.get(allBusyBlocks.size() - 1).end(), TimeRange.END_OF_DAY, true));
    }

    /* Only return available blocks that satisfy request length requirement. */
    return allAvailableBlocks.stream().filter(
        time -> request.getDuration() <= time.duration()).collect(Collectors.toList());
  }
}
