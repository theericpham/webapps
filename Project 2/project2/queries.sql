SELECT COUNT(*) FROM Users;
SELECT COUNT(*) FROM Users WHERE Location= BINARY 'New York';
SELECT COUNT(*) FROM (SELECT ItemID FROM AuctionsCategories GROUP BY ItemID  HAVING COUNT(*) = 4) AS T;
SELECT ItemID FROM Bids WHERE Amount = (SELECT MAX(Amount) FROM Bids WHERE ItemID IN (SELECT ItemID FROM Auctions WHERE Endtime > '2001-12-20 00:00:01'));
SELECT COUNT(*) FROM Users WHERE UserID IN (SELECT DISTINCT SellerID FROM Auctions) AND Rating > 1000;
SELECT COUNT(DISTINCT Auctions.SellerID) FROM Auctions JOIN Bids ON Auctions.SellerID = Bids.BidderID;
SELECT COUNT(DISTINCT CategoryID) FROM Bids JOIN AuctionsCategories ON Bids.ItemID = AuctionsCategories.ItemID WHERE Bids.Amount > 100;
