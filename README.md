# Koruri

<img width="320" height="320" alt="koruri_320px" src="https://github.com/user-attachments/assets/ce849baf-ba91-454d-a2fe-be81379d221f" />

Koruri is a declarative audio processing library built using Jetpack Compose runtime.
It enables you to describe audio processing declaratively using composable functions.

> [!WARNING]
> Koruri is an EXPERIMENTAL project.
> Do not use it in commercial products.

## Demo

You can define waveforms and apply effects to produce various sounds.

https://github.com/user-attachments/assets/2ac955bb-2d55-472d-b4bf-3f6464ac1902

## Requirements

- Min SDK 26 or higher
- Kotlin 2.0.21 or higher

## Usage

### Download

Koruri is available on Maven Central.

```
repositories {
    mavenCentral()
}

dependencies {
    implementation "net.engawapg.lib:koruri:$version"
}
```

The latest version: <img alt="version badge" src="https://img.shields.io/github/v/release/usuiat/Koruri?filter=*.*.*">

### Define audio processing with Composable functions

Koruri's entry points are `KoruriContent()` or `setKoruriContent()`.
Use `KoruriContent()` within UI composable functions, and `setKoruriContent()` elsewhere.

In the entry point's `content`, you can write audio composable with the same notation as the UI composable.
For sound sources, you can use `SineWave()`, `SquareWave()`, `InstrumentNote()`, and others.

The state of the output audio can be managed using `State`.
Changing the value of `State` triggers recomposition, causing the output audio to change.

The following code is an example that plays a sine wave sound when the switch is turned on and allows you to change the frequency using a slider.

```Kotlin
@Composable
fun KoruriSample() {
    var isPlaying by remember { mutableStateOf(false) }
    var frequency by remember { mutableFloatStateOf(1000f) }

    // Audio composable
    KoruriContent {
        if (isPlaying) {
            SineWave { frequency }
        }
    }

    // UI composable
    Column {
        Switch(
            checked = isPlaying,
            onCheckedChange = { isPlaying = it }
        )
        Slider(
            value = frequency,
            onValueChange = { frequency = it },
            valueRange = 200f..2000f,
        )
    }
}
```

https://github.com/user-attachments/assets/0bb11e5d-9efc-4910-b470-a069c1016e80

For more detailed usage instructions, please refer to the [sample app](app/src/main/java/net/engawapg/app/koruri/).

## API Reference

[API Reference🔎](https://usuiat.github.io/Koruri/)

## License

Copyright 2025 usuiat

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
