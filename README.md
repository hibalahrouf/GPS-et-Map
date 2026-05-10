# 📍 Application de Suivi de Position 

## Objectif
Ce TP consiste à créer une application Android simple qui affiche une carte et suit la position de l’utilisateur en temps réel grâce au GPS.

---
## Demo

https://github.com/user-attachments/assets/7ffc5aae-86cc-4010-9f17-fce8da3f0e52

##  Fonctionnalités

- Affichage d’une carte avec **OSMDroid**
- Demande de permission de localisation
- Récupération de la position GPS en temps réel
- Affichage de la position sur la carte
- Ajout d’un marqueur à chaque nouvelle position
- Déplacement et zoom automatique sur la position actuelle
- Alerte si le GPS est désactivé

---

##  Technologies utilisées

- Java (Android)
- LocationManager (GPS)
- OSMDroid (OpenStreetMap)
- Permissions Android (runtime permissions)

---

##  Fonctionnement

1. L’application démarre et affiche une carte centrée sur le Maroc.
2. Elle demande la permission de localisation.
3. Si le GPS est désactivé, une fenêtre demande de l’activer.
4. Une fois activé, l’application récupère les positions GPS.
5. À chaque mise à jour :
   - Un marqueur est ajouté sur la carte
   - La caméra se déplace vers la position actuelle

