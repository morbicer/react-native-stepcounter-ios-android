import CoreMotion
import Foundation

@objc(StepcounterIosAndroid)
class StepcounterIosAndroid: RCTEventEmitter {

  private let pedometer: CMPedometer = CMPedometer()

  var numberOfSteps: Int! = 0

  override func supportedEvents() -> [String]! {
    return ["StepCounter"]
  }

  override static func requiresMainQueueSetup() -> Bool {
    return true
  }

  @objc(isSupported:withRejecter:)
  func isSupported(resolve: RCTPromiseResolveBlock, reject:RCTPromiseRejectBlock) -> Void {
    if CMPedometer.isStepCountingAvailable() {
      resolve(true)
    } else {
      resolve(false)
    }
  }

  @objc
  func startStepCounter() {
    let startDate = Calendar.current.startOfDay(for: Date())
    self.pedometer.startUpdates(from: startDate) { (data, error) in
      guard let pedometerData = data, error == nil else {
        print("There was an error getting the data: \(String(describing: error))")
        return
      }

      let pedDataSteps = pedometerData.numberOfSteps.intValue
      DispatchQueue.main.async {
        if self.numberOfSteps != pedDataSteps {
        self.numberOfSteps = pedDataSteps
            self.sendEvent(withName: "StepCounter", body: ["steps": self.numberOfSteps, "bootTimeMs": Int(startDate.timeIntervalSince1970 * 1000)])
        }
      }
    }
  }

  @objc
  func stopStepCounter() -> Void {
    pedometer.stopUpdates()
    if #available(iOS 10.0, *) {
        pedometer.stopEventUpdates()
    } else {
        // Fallback on earlier versions
    }
  }
}
