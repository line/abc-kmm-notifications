//
//  ABCNotificationsTest.swift
//  iosAppTests
//
//  Created by Steve Kim on 2021/05/06.
//  Copyright © 2021 orgName. All rights reserved.
//

import shared
import XCTest

class ABCNotificationsTest: XCTestCase {
    
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
        ABCNotifications.Companion()
            .registerSettings {
                $0.add(type: .alert)
                $0.add(type: .badge)
                $0.add(type: .sound)
            }
    }
    
    func testOnMessageReceivedWhenLaunch() {
        let expt = expectation(description: "testOnMessageReceivedWhenLaunch")
        
        ABCNotifications.Companion()
            .onMessageReceived(target: self) {
                guard let payload = try? $0.decodedPayload<PayloadBody>() else { return }
                XCTAssertEqual("sound", payload.aps.sound)
                XCTAssertEqual("body", payload.aps.alert.body)
                XCTAssertEqual("title", payload.aps.alert.title)
                expt.fulfill()
            }.beginListening()
        
        ABCNotificationCenterDelegate.Companion().applicationDidFinishLaunching(options: options)
        
        waitForExpectations(timeout: 5)
    }
    
    func testOnMessageReceivedOnForeground() {
        let userInfo = options[UIApplication.LaunchOptionsKey.remoteNotification]!
        
        let expt = expectation(description: "testOnMessageReceivedOnForeground")
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            
            ABCNotifications.Companion()
                .onMessageReceived(target: self) {
                    guard let payload = try? $0.decodedPayload() else { return }
                    XCTAssertEqual("sound", payload.aps.sound)
                    XCTAssertEqual("body", payload.aps.alert.body)
                    XCTAssertEqual("title", payload.aps.alert.title)
                    expt.fulfill()
                }.beginListening()
            
            ABCNotificationCenterDelegate.Companion().applicationDidReceiveRemoteNotification(userInfo: userInfo)
        }
        
        waitForExpectations(timeout: 5)
    }
}
