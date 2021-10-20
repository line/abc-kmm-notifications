//
//  ABCNotificationCenterDelegateTest.swift
//  iosAppTests
//
//  Created by Steve Kim on 2021/05/04.
//  Copyright © 2021 orgName. All rights reserved.
//

import shared
import XCTest

@testable import iosApp

class ABCNotificationCenterDelegateTest: XCTestCase {
    
    let options = [
        UIApplication.LaunchOptionsKey.remoteNotification: [
            "aps": [
                "sound": "sound",
                "alert": [
                    "body": "body",
                    "title": "title"
                ]
            ]
        ]
    ]
    
    override func setUp() {
        ABCNotifications.Companion().unregister()
    }

    func testApplicationDidFinishLaunchingWithOptions() {
        ABCNotificationCenterDelegate.Companion().applicationDidFinishLaunching(options: options)

        XCTAssertEqual(1, ABCNotifications.Companion().numberOfNotifications)
    }
    
    func testApplicationDidReceiveRemoteNotification() {
        let userInfo = options[UIApplication.LaunchOptionsKey.remoteNotification]!
        
        ABCNotificationCenterDelegate.Companion().applicationDidReceiveRemoteNotification(userInfo: userInfo)

        XCTAssertEqual(1, ABCNotifications.Companion().numberOfNotifications)
    }

    func testBeginListening() {
        ABCNotificationCenterDelegate.Companion().applicationDidFinishLaunching(options: options)

        XCTAssertEqual(1, ABCNotifications.Companion().numberOfNotifications)
        
        ABCNotifications.Companion().beginListening()
        
        XCTAssertEqual(0, ABCNotifications.Companion().numberOfNotifications)
    }
    
    func testApplicationDidReceiveLocalNotification() {
        let notification = UILocalNotification()
        notification.userInfo = options[UIApplication.LaunchOptionsKey.remoteNotification]!
        
        ABCNotificationCenterDelegate.Companion().applicationDidReceive(notification: notification)

        XCTAssertEqual(1, ABCNotifications.Companion().numberOfNotifications)
    }
}
