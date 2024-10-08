/**
 * JetBrains Space Automation
 * This Kotlin script file lets you automate build activities
 * For more info, see https://www.jetbrains.com/help/space/automation.html
 */
job("Build and run tests") {
    container(displayName = "Run gradle build", image = "amazoncorretto:17-alpine") {
        kotlinScript { api ->
            // here can be your complex logic
            api.gradlew("build", "-x", "test")
            api.gradlew("test")
        }
    }
}