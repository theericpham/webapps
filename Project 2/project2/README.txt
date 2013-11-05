                                                                     
                                                                     
                                                                     
                                             
Team Member 1: 
Name: Eric Pham
SID: 303-787-024

Team Member 2:
Name: Neema Oshidary
SID: 403-800-317


PART B: Design your relational schema

1. Relations:

Users(UserID, Rating, Location, Country) 
[PRIMARY KEY: UserID]

Auctions(ItemID, SellerID, Name, StartTime, EndTime, StartPrice, BuyPrice, Description) 
[PRIMARY KEY: ItemID]
[FOREIGN KEY: SellerID REF User(UserID)]

Categories(CategoryID, Name)
[PRIMARY KEY: CategoryID]

Bids(ItemID, BidderID, Time, Amount)
[PRIMARY KEY: ItemID, BidderID, Time]
[FOREIGN KEY: BidderID REF Users(UserID)]
[FOREIGN KEY: ItemID REF Auctions(ItemID)]

AuctionCategories(ItemID, CategoryID)
[PRIMARY KEY: ItemID, CategoryID]
[FOREIGN KEY: ItemID REF Auctions(ItemID)]
[FOREIGN KEY: CategoryID REF Categories(CategoryID)]

2. Functional Dependencies:

UserID -> Rating, Location, Country
ItemID -> SellerID, Name, StartTime, EndTime, StartPrice, BuyPrice, Description, CategoryID
ItemID, BidderID, Time -> Amount
CategoryID -> Name

3. Boyce-Codd Normal Form:

The relational schema that we came up with is in BCNF. The one decision that we made that was
not necessary for making the schema into BCNF was that we created a separate table to map from
a category id to a category name and only stored the category id in the AuctionCategories table.
This way, the table doesn't have to repeat the long text title of a category, but rather just store its
id in the table.

