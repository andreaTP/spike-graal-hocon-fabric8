import org.ekrich.config._
import io.fabric8.kubernetes.client.{ DefaultKubernetesClient, KubernetesClientException }
import io.fabric8.kubernetes.client.Watcher
import io.fabric8.kubernetes.client.Watcher.Action
import io.fabric8.kubernetes.api.model.Pod
// import wvlet.log.Logger
// import wvlet.log.LogLevel

object Main extends App {
  // this doesn't really work all... for no reasons with no output...
  // wvlet.log.Logger.init
  // val logger = Logger("example")
  // logger.setLogLevel(LogLevel("info"))

  // TEST CONFIG
  // QUIRK:
  // Environment variables are resolved at compile time by GraalVM
  val config = ConfigFactory.parseString("""
    foo="Example"

    a=${foo}

    b=${?EXAMPLE}

    c = [ "a", ${?OPTIONAL_A} ]
  """).resolve()

  println(config.getString("a"))
  println(config.getString("b"))
  println(config.getStringList("c"))

  val microk8sconf = io.fabric8.kubernetes.client.Config.fromKubeconfig(
     """|apiVersion: v1
        |clusters:
        |- cluster:
        |    certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURBVENDQWVtZ0F3SUJBZ0lKQU5BYUhwczBENUFFTUEwR0NTcUdTSWIzRFFFQkN3VUFNQmN4RlRBVEJnTlYKQkFNTURERXdMakUxTWk0eE9ETXVNVEFlRncweU1ERXdNRFl4TVRJek5URmFGdzB6TURFd01EUXhNVEl6TlRGYQpNQmN4RlRBVEJnTlZCQU1NRERFd0xqRTFNaTR4T0RNdU1UQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQCkFEQ0NBUW9DZ2dFQkFOaWg1VzBvN01CcmFyVlBpay9WUXRxSTNpSDUxMHl6czRYUWtJTUorZHdLUEc1ZUZBSEcKSTh4L0crRW0rM0VsWThJc210aXNrRWtKVTlkTGVPeGVqTkliRStmbzV1UytBZGJOcXNKaG1XVFU5cnpuMHkzdApYVzhoSG0wVFp3TndwWllIREVNeksrWWgvcEd0ZGMybzNDVnRaT2RSR3BlQ2xuVHZQM21VQnNOYUJxb0VRdXZCCmQzY3o2U2V1SldSaGxEM0NuRk93QTkrbVdnN3Bkb0FXNTNwK1RuSGUxdUx3Vlp2U25LWjFqNmJmaVloUjVZbHYKWXdqZzd3ZnZDa3d1cS85NG04YXpUUFg5SlVXYXBaMmwxejI5SXpUTTNHMUhIWDlxMGN0Zk80UFhhY2JDaEZyZgo1YmZmMEhQMWh1bm4vU05CNzJYaGs0OXVHSjI5czZFMlNGY0NBd0VBQWFOUU1FNHdIUVlEVlIwT0JCWUVGQjV4Cm5sdUkrOHpoT3RVbjhOMFJyRzhrNWMrWk1COEdBMVVkSXdRWU1CYUFGQjV4bmx1SSs4emhPdFVuOE4wUnJHOGsKNWMrWk1Bd0dBMVVkRXdRRk1BTUJBZjh3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUNKcWFac1JoM3pJU2JqZAp0RGJ0TEpOcWZ2OUxRdkdvaVN4RVkwcFRMWXliZ1BRb1NJTEVIYWxrVGJRTi9DdEZqMmtrbVJFd0w2b0ZFNUtUCjNOOXY5M09oamZ6T01KQXVoYUhSVGhUSUl3QTRlVW5yVXZ4VllIOFJYT1RNSUhFVUlmWW01NXJWQ3dwdFVnbHAKQk8vVy9PUlVkMXNjUjdDZi85SnYyc1Brc2dTRzd5S1dtSSs2aWhrSjdFNGlvSi9XYjdqWjJmSVRRUHdvUGxRYwpTRjZqUTdab0xpVXo4bk9iWXJEMVNGREtLbUYxajByQ3UzcGhaS0hxYm9GQTZ4ZzNCMmhCei82YUZTYjhWTUZqCnJ6ZmxBVlEzNHlHb2Z4VjVuUlRzQVlOeHhwN1NqSFZFTXMvam0vN1lGcWZvUnRraHZpN1dWZlhCeU8zYmE0d20KczNuTlBnVT0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=
        |    server: https://192.168.64.3:16443
        |  name: microk8s-cluster
        |contexts:
        |- context:
        |    cluster: microk8s-cluster
        |    user: admin
        |  name: microk8s
        |current-context: microk8s
        |kind: Config
        |preferences: {}
        |users:
        |- name: admin
        |  user:
        |    token: L2o4Ykd5aTNaa0hEUkk0Zno3UEFXOGNOMzhpRTlnRmVKdmIwKzRBbnZZbz0K
        |""".stripMargin)

  // Test kubernetes client
  val client = new DefaultKubernetesClient(microk8sconf)

  val watcher = new Watcher[Pod]() {
    def eventReceived(action: Action, pod: Pod) = {
      println(action + " -> " + pod.getMetadata.getName)
    }
    def onClose(ex: KubernetesClientException): Unit = {
      println("Connection closed")
    }
  }

  client.pods.watch(watcher)

  while (true) Thread.sleep(1000)

}