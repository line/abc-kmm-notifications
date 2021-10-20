
import shared
import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    
    private let isNotTest = ProcessInfo.processInfo.environment["XCTestConfigurationFilePath"] == nil
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        if isNotTest {
            ABCNotifications.Companion().isUseFCMOnIOS = true
            ABCNotifications.Companion()
                .registerSettings {
                    $0.add(type: .alert)
                    $0.add(type: .badge)
                    $0.add(type: .sound)
                }
                .onNewToken(target: self) {
                    print("onNewToken -> ", ABCDeviceToken.Companion().rawToken, ABCDeviceToken.Companion().FCMToken)
                }
                .onMessageReceived(target: self) {
                    guard let payload = try? $0.decodedPayload() else { return }

                    print("onMessageReceived -> ", payload)

                    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                        let vc = UIAlertController(title: payload.aps.alert.title, message: payload.body?.notificationType, preferredStyle: .alert)
                        vc.addAction(.init(title: "Ok", style: .default, handler: nil))
                        application.keyWindow?.rootViewController?.present(vc, animated: true)
                    }
                }.beginListening()

            let userInfo: [String: Any] = [
                "aps": [
                    "category": "NEW_MESSAGE_CATEGORY",
                    "badge": 1,
                    "sound": "",
                ],
                "body": [
                    "notificationType": "orderNotified",
                    "orderId": "",
                ],
                "experienceId": "",
            ]
            var options = launchOptions ?? [:]
            options[UIApplication.LaunchOptionsKey.remoteNotification] = userInfo

            ABCNotificationCenterDelegate.Companion().applicationDidFinishLaunching(options: options)
        }
        
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        if isNotTest {
            ABCNotificationCenterDelegate.Companion().applicationDidRegisterForRemoteNotifications(deviceToken: deviceToken)
        }
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("didFailToRegisterForRemoteNotificationsWithError", error)
    }
    
    // MARK: - Push Notification for iOS 9
    
    func application(_ application: UIApplication, didRegister notificationSettings: UIUserNotificationSettings) {
        if isNotTest {
            ABCNotificationCenterDelegate.Companion().applicationDidRegisterNotification()
        }
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) {
        if isNotTest {
            ABCNotificationCenterDelegate.Companion().applicationDidReceiveRemoteNotification(userInfo: userInfo)
        }
    }
    
    // MARK: - Local Notification
    
    func application(_ application: UIApplication, didReceive notification: UILocalNotification) {
        if isNotTest {
            ABCNotificationCenterDelegate.Companion().applicationDidReceive(notification: notification)
        }
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    
    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        if isNotTest {
            ABCNotificationCenterDelegate.Companion().userNotificationCenterWillPresent(notification: notification)
        }
    }
    
    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        if isNotTest {
            ABCNotificationCenterDelegate.Companion().userNotificationCenterDidReceive(response: response)
        }
    }
}
