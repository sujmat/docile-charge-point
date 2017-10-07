package chargepoint.docile


import scala.language.{higherKinds, postfixOps}
import cats.implicits._
import com.thenewmotion.ocpp.messages._
import test.OcppTest


abstract class TestScript[F[_]] extends OcppTest[F] {

  "connect and send bye" in { ops =>

    for {
      _ <- ops.connect()
      _ <- ops.send(BootNotificationReq(
        chargePointVendor = "NewMotion",
        chargePointModel = "Lolo 1337",
        chargePointSerialNumber = Some("03000001"),
        chargeBoxSerialNumber = Some("03000001"),
        firmwareVersion = Some("1"),
        iccid = None,
        imsi = None,
        meterType = None,
        meterSerialNumber = None)
      )
      _ <- ops.expectIncoming printingTheMessage; // matching { case _: BootNotificationRes => };
      _ <- ops.expectIncoming.requestMatching { case _: GetConfigurationReq => }.respondingWith(GetConfigurationRes(List(KeyValue(key = "aap", readonly = true, value = Some("zlurf"))), List("schaap", "blaat")))
      _ <- ops.expectIncoming.cancelReservationReq.respondingWith(CancelReservationRes(true))// GetLocalListVersionReq

      // // meant to fail, but at least make us wait for above response to arrive
      // _ <- expectIncoming printingTheMessage;
      _ <- ops.disconnect()
    } yield ()
  }

}
