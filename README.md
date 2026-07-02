# LevelUp
Aplicación móvil **funcional** para sistemas Android creada con Android Studio. Simula una aplicación de compra-venta de videojuegos.
Proyecto en **grupo** por iniciativa propia, realizado con propósitos didácticos.
Miembros del grupo: Asier del Cid, David Moreno, Luis Manuel Madrazo, Jia Hao Zhou.
## Características principales
- **Interfaz de registro/inicio de sesión**: almacena la información de los usuarios registrados en una base de datos. Se utiliza una autenticación basada en **credenciales**. Una vez el usuario inicia sesión, sus credenciales se guardan cifradas en la cache para facilitar futuros inicios de sesión.
- **Catálogo de videojuegos**: permite una busqueda de juegos a traves del título, ordenamiento mediante precio o valoración y fitrado por plataforma.
- **Carrito de compras**: gestiona los juegos seleccionados. La interfaz muestra los juegos seleccionados y permite eliminar juegos o realizar la compra.
- **Venta de juegos**: permite subir juegos a la plataforma para ser vendidos, los juegos subidos se almacenan en la base de datos. Permite subir la imágen del juego desde la galería del telefono.
- **Perfil de vendedor**: permite al vendedor consultar sus estadisticas mensuales. Esta parte no esta acabada, utiliza una plantilla con datos fijos.
## Tecnologías utilizadas
- **Entorno de desarrollo**: Android Studio.
- **Lenguaje de programación**: Java.
- **Interfaz**: XML.
- **API**: Django.
- **Almacenamiento**: MongoDB y SharedPreferences (para almacenar en la cache).
## Capturas de pantalla
**No se muestran todas las interfaces**, tan solo las más relevantes.
| Interfaz | Imagen |
| --- | --- |
| Pantalla de inicio | <img width="921" height="2048" alt="WhatsApp Image 2026-07-03 at 01 22 44" src="https://github.com/user-attachments/assets/75e2f30d-500d-4282-931a-27d637803202" />
| Catálogo | <img width="921" height="2048" alt="WhatsApp Image 2026-07-03 at 01 22 44 (1)" src="https://github.com/user-attachments/assets/a161de1e-ce6c-404d-8e57-ec0180a8a227" /> |
| Cesta | <img width="1080" height="2400" alt="WhatsApp Image 2026-07-03 at 01 22 44 (2)" src="https://github.com/user-attachments/assets/318333f2-70fa-4707-8ddc-aa74ba6db21a" /> |
| Venta | <img width="921" height="2048" alt="WhatsApp Image 2026-07-03 at 01 22 44 (3)" src="https://github.com/user-attachments/assets/9f3c95f7-0dd1-4ad3-b51e-afb3fdf96c4e" /> |
| Perfil de vendedor | <img width="921" height="2048" alt="WhatsApp Image 2026-07-03 at 01 22 45" src="https://github.com/user-attachments/assets/d23a32c6-f669-4ea8-83c4-7632b2d93581" /> |
## Requsitos
Para poder compilar este proyecto es necesario:
- [Android Studio](https://developer.adroid.com/studio)
- SDK con una version superior a la 24
