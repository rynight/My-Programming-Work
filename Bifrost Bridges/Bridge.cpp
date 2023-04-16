// Fill out your copyright notice in the Description page of Project Settings.


#include "Bridge.h"
#include "Kismet/GameplayStatics.h"
#include <cstdlib>
#include "Misc/SingleThreadEvent.h"

ABridge::ABridge()
{
	//When any actor enters a bridge call the EnterTeleporter function
	OnActorBeginOverlap.AddDynamic(this, &ABridge::EnterTeleporter);
}

void ABridge::BeginPlay()
{
	Super::BeginPlay();

	//Find character
	for (TActorIterator<ABifrostCharacter> ActorItr1(GetWorld()); ActorItr1; ++ActorItr1)
	{
		character1 = *ActorItr1;

		curDim = character1->curDim;
	}

	//Bool for whether dims are flipped
	canUse = true;
	flipped = false;
	//Arrays to contain all dimPins
	ADimensionalPin* dimPins1[100] = { nullptr };
	ADimensionalPin* dimPins2[100] = { nullptr };

	//Add all dim pins to list
	for (TActorIterator<ADimensionalPin> ActorItr1(GetWorld()); ActorItr1; ++ActorItr1)
	{
		ADimensionalPin* curDimPin = *ActorItr1;

		switch (curDimPin->Dim)
		{
		case 1:
			dimPins1[curDimPin->Region] = curDimPin;
			break;

		case 2:
			dimPins2[curDimPin->Region] = curDimPin;
			break;
		}
	}

	FVector playerLocation = character1->GetActorLocation();
	FVector tempDimPinLocWow = dimPins1[0]->GetActorLocation();
	if (abs(playerLocation.X - tempDimPinLocWow.X) + abs(playerLocation.Y - tempDimPinLocWow.Y) + abs(playerLocation.Z - tempDimPinLocWow.Z) < 500000)
	{
		character1->curDim = 1;
		curDim = 1;
		//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("BRIDGE IN DIM 1")));
	}
	else
	{
		character1->curDim = 2;
		curDim = 2;
		//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("BRIDGE IN DIM 2")));
	}

	//Find dim pins closest to player location
	int curClosestPin = 0;
	float closestPinDist = 0;
	FVector firstPinLoc;
	switch (curDim)
	{
	case 1:
		firstPinLoc = dimPins1[0]->GetActorLocation();
		closestPinDist = abs(playerLocation.X - firstPinLoc.X) + abs(playerLocation.Y - firstPinLoc.Y) + abs(playerLocation.Z - firstPinLoc.Z);
		break;

	case 2:
		firstPinLoc = dimPins2[0]->GetActorLocation();
		closestPinDist = abs(playerLocation.X - firstPinLoc.X) + abs(playerLocation.Y - firstPinLoc.Y) + abs(playerLocation.Z - firstPinLoc.Z);
		break;
	}

	for (int i = 1; i < 100; i++)
	{
		if (dimPins1[i] == nullptr)
		{
			continue;
		}

		FVector curPinLoc;
		float curDist = 10000000000000.0;

		switch (curDim)
		{
		case 1:
			curPinLoc = dimPins1[i]->GetActorLocation();
			
			break;
		case 2:
			curPinLoc = dimPins2[i]->GetActorLocation();
			break;
		}

		curDist = abs(playerLocation.X - curPinLoc.X) + abs(playerLocation.Y - curPinLoc.Y) + abs(playerLocation.Z - curPinLoc.Z);
		//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("For Region %d, dist away is: %f"), i, curDist));
		if (curDist < closestPinDist)
		{
			closestPinDist = curDist;
			curClosestPin = i;
		}
	}

	//Set closest dim pins
	PinDim1 = dimPins1[curClosestPin]->GetActorLocation();
	PinDim2 = dimPins2[curClosestPin]->GetActorLocation();

	//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("Player Location is: %f, %f, %f"), playerLocation.X, playerLocation.Y, playerLocation.Z));
	//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("Region: %d"), curClosestPin));

	//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("closestdist: %f"), closestPinDist));

	//Gets location of bridge in other dimension
	switch (curDim)
	{
	case 1:
		BridgeLoc = GetActorLocation();
		OtherSideLoc = BridgeLoc - PinDim1 + PinDim2;
		break;

	case 2:
		BridgeLoc = GetActorLocation();
		OtherSideLoc = BridgeLoc - PinDim2 + PinDim1;
		break;
	}

	if (flipped)
	{
		OtherSideLoc.Z = -OtherSideLoc.Z;
	}
}

/**
 * Function called whenever any actor enters a bridge collision box.
 * 
 * Function will take actor and change position to the opposite dimension.
 */
void ABridge::EnterTeleporter(class AActor* overlappedActor, class AActor* otherActor)
{
	if (otherActor && otherActor != this)
	{
		//The player character
		ABifrostCharacter* character = Cast<ABifrostCharacter>(otherActor);

		if (character && canUse)
		{
			canUse = false;
			//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("Player Location is:")));
			if (flipped)
			{
				OtherSideLoc.Z = -OtherSideLoc.Z;
			}

			FVector curPlayerLoc = character->GetActorLocation();
			FVector newPlayerLoc = curPlayerLoc - BridgeLoc + OtherSideLoc;

			if (flipped)
			{
				OtherSideLoc.Z = -OtherSideLoc.Z;
				newPlayerLoc.Z = -newPlayerLoc.Z;
			}

			

			if (abs(curPlayerLoc.X - PinDim1.X) + abs(curPlayerLoc.Y - PinDim1.Y) + abs(curPlayerLoc.Z - PinDim1.Z) < 500000)
			{
				//Player is in dim 1, trying to teleport to dim 2
				if (abs(newPlayerLoc.X - PinDim2.X) + abs(newPlayerLoc.Y - PinDim2.Y) + abs(newPlayerLoc.Z - PinDim2.Z) < 500000)
				{
					//If location to teleport to is in the other dimension, teleport
					character->SetActorLocation(newPlayerLoc);
					character->curDim = 2;
				}
				else
				{
					//Sometimes Bridge location is glitched and this fixes that
					newPlayerLoc = curPlayerLoc + BridgeLoc - OtherSideLoc;
					if (abs(newPlayerLoc.X - PinDim2.X) + abs(newPlayerLoc.Y - PinDim2.Y) + abs(newPlayerLoc.Z - PinDim2.Z) < 500000)
					{
						//If location to teleport to is in the other dimension, teleport
						character->SetActorLocation(newPlayerLoc);
						character->curDim = 2;
					}
					else
					{
						//If we hit this, we fucked up
						//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("BRIDGE IN DIM 1\nPlayer Location is: %f, %f, %f\nBridge location is %f, %f, %f\nPin2 location is %f, %f, %f"), curPlayerLoc.X, curPlayerLoc.Y, curPlayerLoc.Z, BridgeLoc.X, BridgeLoc.Y, BridgeLoc.Z, PinDim2.X, PinDim2.Y, PinDim2.Z));
					}
				}
			}
			else
			{
				//Player is in dim 2, trying to teleport to dim 1
				if (abs(newPlayerLoc.X - PinDim1.X) + abs(newPlayerLoc.Y - PinDim1.Y) + abs(newPlayerLoc.Z - PinDim1.Z) < 500000)
				{
					//If location to teleport to is in the other dimension, teleport
					character->SetActorLocation(newPlayerLoc);
					character->curDim = 1;
				}
				else
				{
					//Sometimes Bridge location is glitched and this fixes that
					newPlayerLoc = curPlayerLoc + BridgeLoc - OtherSideLoc;
					if (abs(newPlayerLoc.X - PinDim1.X) + abs(newPlayerLoc.Y - PinDim1.Y) + abs(newPlayerLoc.Z - PinDim1.Z) < 500000)
					{
						//If location to teleport to is in the other dimension, teleport
						character->SetActorLocation(newPlayerLoc);
						character->curDim = 1;
					}
					else
					{
						//If we hit this, we fucked up
						//GEngine->AddOnScreenDebugMessage(-1, 15.0f, FColor::Yellow, FString::Printf(TEXT("BRIDGE IN DIM 2\nPlayer Location is: %f, %f, %f\nBridge location is %f, %f, %f\nPin1 location is %f, %f, %f"), curPlayerLoc.X, curPlayerLoc.Y, curPlayerLoc.Z, BridgeLoc.X, BridgeLoc.Y, BridgeLoc.Z, PinDim1.X, PinDim1.Y, PinDim1.Z));
					}
				}
			}
		}
	}
}